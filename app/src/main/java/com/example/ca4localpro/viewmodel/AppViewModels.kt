package com.example.ca4localpro.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca4localpro.data.auth.AuthRepository
import com.example.ca4localpro.data.auth.DefaultAuthRepository
import com.example.ca4localpro.di.Injection
import com.example.ca4localpro.data.local.AuthSessionManager
import com.example.ca4localpro.data.model.*
import com.example.ca4localpro.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val authRepository: AuthRepository = DefaultAuthRepository(
        authDataSource = Injection.provideAuthDataSource(),
        sessionManager = AuthSessionManager(application)
    )
) : AndroidViewModel(application) {

    val authState: StateFlow<AuthState> = authRepository.authState

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var loginEmail = MutableStateFlow("")
    var loginPassword = MutableStateFlow("")
    var authError = MutableStateFlow<String?>(null)
    var isLoading = MutableStateFlow(false)

    var registerName = MutableStateFlow("")
    var registerEmail = MutableStateFlow("")
    var registerPhone = MutableStateFlow("")
    var registerPassword = MutableStateFlow("")
    var registerConfirmPassword = MutableStateFlow("")
    var registerRole = MutableStateFlow(UserRole.CUSTOMER)

    fun checkSession() {
        viewModelScope.launch {
            authRepository.checkSession()
        }
    }

    fun login(onSuccess: () -> Unit) {
        val email = loginEmail.value.trim()
        val password = loginPassword.value

        if (email.isBlank()) {
            authError.value = "Please enter your email address"
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            authError.value = "Please enter a valid email address"
            return
        }
        if (password.length < 6) {
            authError.value = "Password must be at least 6 characters"
            return
        }

        isLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                authError.value = exception.message ?: "Authentication failed. Please try again."
            }
        }
    }

    fun register(onSuccess: () -> Unit) {
        val name = registerName.value.trim()
        val email = registerEmail.value.trim()
        val phone = registerPhone.value.trim()
        val password = registerPassword.value
        val confirmPassword = registerConfirmPassword.value
        val role = registerRole.value

        if (name.isBlank()) {
            authError.value = "Please enter your full name"
            return
        }
        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            authError.value = "Please enter a valid email address"
            return
        }
        if (phone.isBlank() || phone.length < 10) {
            authError.value = "Please enter a valid 10-digit phone number"
            return
        }
        if (password.length < 6) {
            authError.value = "Password must be at least 6 characters"
            return
        }
        if (confirmPassword.isNotEmpty() && password != confirmPassword) {
            authError.value = "Passwords do not match"
            return
        }
        if (role == UserRole.ADMIN) {
            authError.value = "Admin account creation is restricted"
            return
        }

        isLoading.value = true
        authError.value = null

        viewModelScope.launch {
            val result = authRepository.register(name, email, phone, password, role)
            isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { exception ->
                authError.value = exception.message ?: "Registration failed. Please try again."
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            loginEmail.value = ""
            loginPassword.value = ""
            registerName.value = ""
            registerEmail.value = ""
            registerPhone.value = ""
            registerPassword.value = ""
            registerConfirmPassword.value = ""
            authError.value = null
            onSuccess()
        }
    }

    fun switchRole() {
        authRepository.switchRole()
    }
}

// Home ViewModel
class HomeViewModel(
    private val serviceRepository: ServiceRepository = Injection.provideServiceRepository()
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ServiceCategory>>(emptyList())
    val categories: StateFlow<List<ServiceCategory>> = _categories.asStateFlow()

    private val _nearbyProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val nearbyProviders: StateFlow<List<ServiceProvider>> = _nearbyProviders.asStateFlow()

    private val _recommendedProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val recommendedProviders: StateFlow<List<ServiceProvider>> = _recommendedProviders.asStateFlow()

    private val _emergencyProviders = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val emergencyProviders: StateFlow<List<ServiceProvider>> = _emergencyProviders.asStateFlow()

    val currentAddress = MutableStateFlow("42 Park Street, Connaught Place")
    val searchQuery = MutableStateFlow("")

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _categories.value = serviceRepository.getCategories().getOrDefault(emptyList())
            _nearbyProviders.value = serviceRepository.getNearbyProviders().getOrDefault(emptyList())
            _recommendedProviders.value = serviceRepository.getRecommendedProviders().getOrDefault(emptyList())
            _emergencyProviders.value = serviceRepository.getEmergencyProviders().getOrDefault(emptyList())
        }
    }
}

// Provider ViewModel
class ProviderViewModel(
    private val serviceRepository: ServiceRepository = Injection.provideServiceRepository()
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)

    private val _providersList = MutableStateFlow<List<ServiceProvider>>(emptyList())
    val providersList: StateFlow<List<ServiceProvider>> = _providersList.asStateFlow()

    private val _selectedProvider = MutableStateFlow<ServiceProvider?>(null)
    val selectedProvider: StateFlow<ServiceProvider?> = _selectedProvider.asStateFlow()

    init {
        loadAllProviders()
    }

    fun loadAllProviders() {
        viewModelScope.launch {
            _providersList.value = serviceRepository.getNearbyProviders().getOrDefault(emptyList())
        }
    }

    fun filterByCategory(categoryId: String?) {
        selectedCategory.value = categoryId
        viewModelScope.launch {
            if (categoryId.isNullOrEmpty()) {
                _providersList.value = serviceRepository.getNearbyProviders().getOrDefault(emptyList())
            } else {
                _providersList.value = serviceRepository.getProvidersByCategory(categoryId).getOrDefault(emptyList())
            }
        }
    }

    fun search(query: String) {
        searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                filterByCategory(selectedCategory.value)
            } else {
                _providersList.value = serviceRepository.searchProviders(query).getOrDefault(emptyList())
            }
        }
    }

    fun selectProviderById(providerId: String) {
        viewModelScope.launch {
            _selectedProvider.value = serviceRepository.getProviderById(providerId).getOrNull()
        }
    }
}
