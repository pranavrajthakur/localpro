package com.example.ca4localpro.data.remote

import com.example.ca4localpro.data.model.ServiceItem
import com.example.ca4localpro.data.model.ServiceProvider
import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole
import com.example.ca4localpro.data.repository.UserRepository
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUpdateDto(
    val name: String,
    val phone: String?,
    // address/location is currently mixed, adding primary_address logically
    // To match actual schema we just store what we have or update profiles table
)

class RemoteUserRepository : UserRepository {
    private val supabase = SupabaseClientProvider.client

    override suspend fun updateCustomerProfile(
        userId: String,
        name: String,
        phone: String,
        address: String,
        avatarUrl: String?
    ): Result<UserProfile> {
        return try {
            val updateData = mapOf(
                "name" to name,
                "phone" to phone,
                "profile_image" to avatarUrl,
                "primary_address" to address
            )
            
            val updated = supabase.from("profiles").update(updateData) {
                select()
                filter { eq("id", userId) }
            }.decodeSingle<com.example.ca4localpro.data.auth.ProfileDto>()
            
            Result.success(
                UserProfile(
                    id = updated.id,
                    name = updated.name,
                    email = updated.email,
                    phone = updated.phone ?: "",
                    role = UserRole.valueOf(updated.role.uppercase()),
                    primaryAddress = updated.primary_address ?: address,
                    avatarUrl = updated.profile_image
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProviderProfile(userId: String): Result<ServiceProvider?> {
        return try {
            val dto = supabase.from("providers").select { filter { eq("id", userId) } }.decodeSingleOrNull<ProviderDto>()
            if (dto == null) return Result.success(null)
            
            Result.success(mapProviderDto(dto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProviderProfile(
        providerId: String,
        businessName: String,
        description: String,
        phone: String,
        experienceYears: Int,
        serviceRadius: Int,
        locationAddress: String,
        avatarUrl: String?
    ): Result<ServiceProvider> {
        return try {
            val updateData = mapOf(
                "business_name" to businessName,
                "description" to description,
                "phone" to phone,
                "experience_years" to experienceYears,
                "service_radius" to serviceRadius,
                "location_address" to locationAddress,
                "profile_image" to avatarUrl
            )
            
            val updatedDto = supabase.from("providers").update(updateData) {
                select()
                filter { eq("id", providerId) }
            }.decodeSingle<ProviderDto>()
            
            Result.success(mapProviderDto(updatedDto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProviderAvailability(providerId: String, isAvailable: Boolean): Result<Unit> {
        return try {
            supabase.from("providers").update(mapOf("is_available" to isAvailable)) {
                filter { eq("id", providerId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProviderServices(providerId: String): Result<List<ServiceItem>> {
        return try {
            val dtos = supabase.from("provider_services").select {
                filter { eq("provider_id", providerId) }
            }.decodeList<ProviderServiceDto>()
            
            Result.success(dtos.map { 
                ServiceItem(
                    id = it.id,
                    title = it.title,
                    description = it.description,
                    price = it.price,
                    durationText = it.duration_text
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProviderService(
        providerId: String,
        title: String,
        description: String,
        price: Double,
        durationText: String
    ): Result<ServiceItem> {
        return try {
            val insertData = mapOf(
                "provider_id" to providerId,
                "title" to title,
                "description" to description,
                "price" to price,
                "duration_text" to durationText
            )
            val dto = supabase.from("provider_services").insert(insertData).decodeSingle<ProviderServiceDto>()
            
            Result.success(
                ServiceItem(
                    id = dto.id,
                    title = dto.title,
                    description = dto.description,
                    price = dto.price,
                    durationText = dto.duration_text
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeProviderService(serviceId: String): Result<Unit> {
        return try {
            supabase.from("provider_services").delete {
                filter { eq("id", serviceId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapProviderDto(dto: ProviderDto): ServiceProvider {
        return ServiceProvider(
            id = dto.id,
            name = dto.business_name,
            avatarUrl = dto.profile_image,
            categoryId = dto.category,
            categoryName = dto.category,
            rating = dto.rating,
            reviewCount = dto.review_count,
            distanceKm = dto.service_radius?.toDouble() ?: 0.0, // Using distanceKm to hold service_radius temporarily since it's the closest field
            startingPrice = dto.starting_price,
            isVerified = dto.is_verified,
            isEmergencyAvailable = dto.is_available,
            yearsExperience = dto.experience_years,
            bio = dto.description,
            locationAddress = dto.location_address ?: "",
            phone = dto.phone,
            servicesOffered = emptyList(), 
            reviews = emptyList()
        )
    }
}
