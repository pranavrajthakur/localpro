package com.example.ca4localpro.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ca4localpro.data.model.ServiceProvider
import com.example.ca4localpro.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProviderDashboardViewModel(
    application: android.app.Application
) : androidx.lifecycle.AndroidViewModel(application) {

    private val userRepository: UserRepository = com.example.ca4localpro.di.Injection.provideUserRepository()

    private val _uiState = MutableStateFlow<ProviderDashboardUiState>(ProviderDashboardUiState.Loading)
    val uiState: StateFlow<ProviderDashboardUiState> = _uiState.asStateFlow()

    fun loadProviderProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = ProviderDashboardUiState.Loading
            val result = userRepository.getProviderProfile(userId)
            result.onSuccess { provider ->
                if (provider != null) {
                    val servicesResult = userRepository.getProviderServices(userId)
                    val services = servicesResult.getOrNull() ?: emptyList()
                    _uiState.value = ProviderDashboardUiState.Success(provider.copy(servicesOffered = services))
                } else {
                    _uiState.value = ProviderDashboardUiState.Empty
                }
            }.onFailure {
                _uiState.value = ProviderDashboardUiState.Error("Failed to load provider profile.")
            }
        }
    }

    fun updateAvailability(isAvailable: Boolean) {
        val currentState = _uiState.value
        if (currentState is ProviderDashboardUiState.Success) {
            val provider = currentState.provider
            // Optimistic update
            _uiState.value = ProviderDashboardUiState.Success(provider.copy(isEmergencyAvailable = isAvailable))
            
            viewModelScope.launch {
                val result = userRepository.updateProviderAvailability(provider.id, isAvailable)
                result.onFailure {
                    // Revert on failure
                    _uiState.value = ProviderDashboardUiState.Success(provider)
                    // Could also show a temporary error message
                }
            }
        }
    }

    fun updateProfile(
        businessName: String,
        description: String,
        phone: String,
        experienceYears: Int,
        serviceRadius: Int,
        locationAddress: String
    ) {
        val currentState = _uiState.value
        if (currentState is ProviderDashboardUiState.Success) {
            val provider = currentState.provider
            _uiState.value = ProviderDashboardUiState.Saving
            
            viewModelScope.launch {
                val result = userRepository.updateProviderProfile(
                    providerId = provider.id,
                    businessName = businessName,
                    description = description,
                    phone = phone,
                    experienceYears = experienceYears,
                    serviceRadius = serviceRadius,
                    locationAddress = locationAddress,
                    avatarUrl = provider.avatarUrl
                )
                
                result.onSuccess { updatedProvider ->
                    val servicesResult = userRepository.getProviderServices(provider.id)
                    val services = servicesResult.getOrNull() ?: emptyList()
                    _uiState.value = ProviderDashboardUiState.Success(updatedProvider.copy(servicesOffered = services))
                }.onFailure {
                    _uiState.value = ProviderDashboardUiState.Error("Unable to update your profile. Please try again.")
                }
            }
        }
    }

    fun addService(title: String, description: String, price: Double, durationText: String) {
        val currentState = _uiState.value
        if (currentState is ProviderDashboardUiState.Success) {
            val provider = currentState.provider
            
            viewModelScope.launch {
                val result = userRepository.addProviderService(
                    providerId = provider.id,
                    title = title,
                    description = description,
                    price = price,
                    durationText = durationText
                )
                
                result.onSuccess { newService ->
                    val updatedServices = provider.servicesOffered + newService
                    _uiState.value = ProviderDashboardUiState.Success(provider.copy(servicesOffered = updatedServices))
                }
            }
        }
    }

    fun removeService(serviceId: String) {
        val currentState = _uiState.value
        if (currentState is ProviderDashboardUiState.Success) {
            val provider = currentState.provider
            val updatedServices = provider.servicesOffered.filter { it.id != serviceId }
            _uiState.value = ProviderDashboardUiState.Success(provider.copy(servicesOffered = updatedServices))
            
            viewModelScope.launch {
                userRepository.removeProviderService(serviceId)
            }
        }
    }
}

sealed class ProviderDashboardUiState {
    object Loading : ProviderDashboardUiState()
    data class Success(val provider: ServiceProvider) : ProviderDashboardUiState()
    object Saving : ProviderDashboardUiState()
    data class Error(val message: String) : ProviderDashboardUiState()
    object Empty : ProviderDashboardUiState()
}
