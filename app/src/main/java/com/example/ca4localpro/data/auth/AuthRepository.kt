package com.example.ca4localpro.data.auth

import com.example.ca4localpro.data.local.AuthSessionManager
import com.example.ca4localpro.data.model.AuthState
import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

interface AuthRepository {
    val authState: StateFlow<AuthState>
    val currentUser: Flow<UserProfile?>
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun register(name: String, email: String, phone: String, confirmPassword: String, role: UserRole): Result<UserProfile>
    suspend fun checkSession(): AuthState
    suspend fun logout()
    fun switchRole()
}

class DefaultAuthRepository(
    private val authDataSource: AuthDataSource,
    private val sessionManager: AuthSessionManager
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override val currentUser: Flow<UserProfile?> = _authState.map { state ->
        if (state is AuthState.Authenticated) state.user else null
    }

    init {
        // Initialize state based on local session
        val user = sessionManager.getSessionUser()
        if (user != null) {
            _authState.value = AuthState.Authenticated(user)
        }
    }

    override suspend fun checkSession(): AuthState {
        _authState.value = AuthState.CheckingSession
        val user = sessionManager.getSessionUser()
        val state = if (user != null) {
            AuthState.Authenticated(user)
        } else {
            AuthState.Unauthenticated
        }
        _authState.value = state
        return state
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        _authState.value = AuthState.Authenticating

        val result = authDataSource.authenticate(email, password)
        return result.onSuccess { user ->
            sessionManager.saveSession(user)
            _authState.value = AuthState.Authenticated(user)
        }.onFailure { exception ->
            _authState.value = AuthState.AuthenticationError(exception.message ?: "Authentication failed")
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        confirmPassword: String,
        role: UserRole
    ): Result<UserProfile> {
        _authState.value = AuthState.Authenticating

        val result = authDataSource.register(name, email, phone, confirmPassword, role)
        return result.onSuccess { user ->
            sessionManager.saveSession(user)
            _authState.value = AuthState.Authenticated(user)
        }.onFailure { exception ->
            _authState.value = AuthState.AuthenticationError(exception.message ?: "Registration failed")
        }
    }

    override suspend fun logout() {
        sessionManager.clearSession()
        authDataSource.logout()
        _authState.value = AuthState.Unauthenticated
    }

    override fun switchRole() {
        val currentState = _authState.value
        if (currentState is AuthState.Authenticated) {
            val current = currentState.user
            val newRole = if (current.role == UserRole.CUSTOMER) UserRole.PROVIDER else UserRole.CUSTOMER
            val updated = current.copy(role = newRole)
            sessionManager.saveSession(updated)
            _authState.value = AuthState.Authenticated(updated)
        }
    }
}
