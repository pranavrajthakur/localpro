package com.example.ca4localpro.data.model

import androidx.compose.runtime.Immutable

enum class UserRole {
    CUSTOMER,
    PROVIDER,
    ADMIN
}

@Immutable
data class ServiceCategory(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val providerCount: Int = 12
)

@Immutable
data class ServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val durationText: String
)

@Immutable
data class Review(
    val id: String,
    val customerName: String,
    val rating: Float,
    val date: String,
    val comment: String
)

@Immutable
data class ServiceProvider(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val categoryId: String,
    val categoryName: String,
    val rating: Float,
    val reviewCount: Int,
    val distanceKm: Double,
    val startingPrice: Double,
    val isVerified: Boolean = true,
    val isEmergencyAvailable: Boolean = false,
    val yearsExperience: Int,
    val bio: String,
    val locationAddress: String,
    val phone: String,
    val servicesOffered: List<ServiceItem> = emptyList(),
    val reviews: List<Review> = emptyList()
)

@Immutable
data class BookingSummary(
    val id: String,
    val providerName: String,
    val serviceName: String,
    val date: String,
    val time: String,
    val status: String,
    val totalCost: Double
)

@Immutable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole = UserRole.CUSTOMER,
    val primaryAddress: String = "123 Main Street, Downtown",
    val avatarUrl: String? = null
)
