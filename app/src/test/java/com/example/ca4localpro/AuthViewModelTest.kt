package com.example.ca4localpro

import android.app.Application
import com.example.ca4localpro.data.auth.AuthRepository
import com.example.ca4localpro.data.model.AuthState
import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole
import com.example.ca4localpro.viewmodel.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class FakeAuthRepository : AuthRepository {
    override val authState: StateFlow<AuthState> = MutableStateFlow(AuthState.Unauthenticated)
    override val currentUser: Flow<UserProfile?> = MutableStateFlow(null)
    
    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return Result.success(UserProfile("1", "Test", email, "123", UserRole.CUSTOMER, ""))
    }
    
    override suspend fun register(name: String, email: String, phone: String, confirmPassword: String, role: UserRole): Result<UserProfile> {
        return Result.success(UserProfile("1", name, email, phone, role, ""))
    }
    
    override suspend fun checkSession(): AuthState = AuthState.Unauthenticated
    override suspend fun logout() {}
    override fun switchRole() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = FakeAuthRepository()
        viewModel = AuthViewModel(application = Application(), authRepository = authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_withEmptyEmail_setsError() {
        viewModel.loginEmail.value = ""
        viewModel.loginPassword.value = "password123"

        viewModel.login { }

        assertEquals("Please enter your email address", viewModel.authError.value)
    }

    @Test
    fun login_withInvalidEmailFormat_setsError() {
        viewModel.loginEmail.value = "invalidemail"
        viewModel.loginPassword.value = "password123"

        viewModel.login { }

        assertEquals("Please enter a valid email address", viewModel.authError.value)
    }

    @Test
    fun login_withShortPassword_setsError() {
        viewModel.loginEmail.value = "test@example.com"
        viewModel.loginPassword.value = "123"

        viewModel.login { }

        assertEquals("Password must be at least 6 characters", viewModel.authError.value)
    }

    @Test
    fun register_withPasswordMismatch_setsError() {
        viewModel.registerName.value = "Rahul Sharma"
        viewModel.registerEmail.value = "rahul@example.com"
        viewModel.registerPhone.value = "9876543210"
        viewModel.registerPassword.value = "password123"
        viewModel.registerConfirmPassword.value = "different123"

        viewModel.register { }

        assertEquals("Passwords do not match", viewModel.authError.value)
    }

    @Test
    fun register_withAdminRole_setsError() {
        viewModel.registerName.value = "Admin User"
        viewModel.registerEmail.value = "admin@example.com"
        viewModel.registerPhone.value = "9876543210"
        viewModel.registerPassword.value = "password123"
        viewModel.registerConfirmPassword.value = "password123"
        viewModel.registerRole.value = UserRole.ADMIN

        viewModel.register { }

        assertEquals("Admin account creation is restricted", viewModel.authError.value)
    }

    @Test
    fun logout_clearsCurrentUserAndError() {
        runTest {
            viewModel.logout { }
            testDispatcher.scheduler.advanceUntilIdle()

            assertNull(viewModel.authError.value)
            assertEquals("", viewModel.loginEmail.value)
        }
    }
}
