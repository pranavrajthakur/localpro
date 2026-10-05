package com.example.ca4localpro.data.repository

import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.ServiceProvider
import com.example.ca4localpro.data.model.ServiceItem

interface UserRepository {
    suspend fun updateCustomerProfile(userId: String, name: String, phone: String, address: String, avatarUrl: String?): Result<UserProfile>
    
    suspend fun getProviderProfile(userId: String): Result<ServiceProvider?>
    
    suspend fun updateProviderProfile(
        providerId: String, 
        businessName: String, 
        description: String, 
        phone: String, 
        experienceYears: Int, 
        serviceRadius: Int, 
        locationAddress: String,
        avatarUrl: String?
    ): Result<ServiceProvider>

    suspend fun updateProviderAvailability(providerId: String, isAvailable: Boolean): Result<Unit>
    
    suspend fun getProviderServices(providerId: String): Result<List<ServiceItem>>
    
    suspend fun addProviderService(providerId: String, title: String, description: String, price: Double, durationText: String): Result<ServiceItem>
    
    suspend fun removeProviderService(serviceId: String): Result<Unit>
}