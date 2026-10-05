package com.example.ca4localpro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    application: android.app.Application
) : androidx.lifecycle.AndroidViewModel(application) {

    private val userRepository: UserRepository = com.example.ca4localpro.di.Injection.provideUserRepository()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Empty)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(user: UserProfile?) {
        if (user != null) {
            _uiState.value = ProfileUiState.Success(user)
        } else {
            _uiState.value = ProfileUiState.Empty
        }
    }

    fun updateProfile(name: String, phone: String, address: String) {
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            val user = currentState.user
            _uiState.value = ProfileUiState.Saving
            viewModelScope.launch {
                val result = userRepository.updateCustomerProfile(
                    userId = user.id,
                    name = name,
                    phone = phone,
                    address = address,
                    avatarUrl = user.avatarUrl
                )
                
                result.onSuccess { updatedUser ->
                    // Normally we should also update AuthRepository current session user.
                    // Assuming for now we just show success.
                    _uiState.value = ProfileUiState.Success(updatedUser)
                }.onFailure { e ->
                    _uiState.value = ProfileUiState.Error("Unable to update your profile. Please try again.")
                }
            }
        }
    }
}

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: UserProfile) : ProfileUiState()
    object Saving : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Empty : ProfileUiState()
}
