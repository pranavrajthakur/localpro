package com.example.ca4localpro.data.remote

import com.example.ca4localpro.data.model.Review
import com.example.ca4localpro.data.model.ServiceCategory
import com.example.ca4localpro.data.model.ServiceItem
import com.example.ca4localpro.data.model.ServiceProvider
import com.example.ca4localpro.data.repository.ServiceRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val is_active: Boolean,
    val provider_count: Int? = 0
)

@Serializable
data class ProviderDto(
    val id: String,
    val business_name: String,
    val description: String,
    val category: String,
    val experience_years: Int,
    val service_radius: Int? = null,
    val phone: String,
    val profile_image: String?,
    val rating: Float,
    val review_count: Int,
    val starting_price: Double,
    val is_verified: Boolean,
    val is_available: Boolean,
    val location_address: String?
)

@Serializable
data class ProviderServiceDto(
    val id: String,
    val provider_id: String,
    val title: String,
    val description: String,
    val price: Double,
    val duration_text: String
)

class RemoteServiceRepository : ServiceRepository {
    private val supabase = SupabaseClientProvider.client

    override suspend fun getCategories(): Result<List<ServiceCategory>> {
        return try {
            val dtos: List<CategoryDto> = supabase.postgrest["services"].select().decodeList<CategoryDto>()
            val models = dtos.map { 
                ServiceCategory(
                    id = it.id,
                    title = it.name,
                    description = it.description,
                    iconName = it.icon,
                    providerCount = it.provider_count ?: 0
                )
            }
            Result.success(models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyProviders(): Result<List<ServiceProvider>> {
        return fetchProviders()
    }

    override suspend fun getRecommendedProviders(): Result<List<ServiceProvider>> {
        return fetchProviders()
    }

    override suspend fun getEmergencyProviders(): Result<List<ServiceProvider>> {
        return try {
            val dtos: List<ProviderDto> = supabase.postgrest["providers"].select().decodeList<ProviderDto>()
            Result.success(dtos.filter { it.is_available }.map { mapProviderDto(it) })
        } catch(e: Exception) { Result.failure(e) }
    }

    override suspend fun getProvidersByCategory(categoryId: String): Result<List<ServiceProvider>> {
        return try {
            val dtos: List<ProviderDto> = supabase.postgrest["providers"].select().decodeList<ProviderDto>()
            Result.success(dtos.filter { it.category == categoryId }.map { mapProviderDto(it) })
        } catch(e: Exception) { Result.failure(e) }
    }

    override suspend fun getProviderById(id: String): Result<ServiceProvider?> {
        return try {
            val dtos: List<ProviderDto> = supabase.postgrest["providers"].select().decodeList<ProviderDto>()
            val dto = dtos.find { it.id == id }
            if (dto == null) return Result.success(null)
            
            val provider = mapProviderDto(dto)
            Result.success(provider)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProviders(query: String): Result<List<ServiceProvider>> {
        return fetchProviders()
    }

    private suspend fun fetchProviders(): Result<List<ServiceProvider>> {
        return try {
            val dtos: List<ProviderDto> = supabase.postgrest["providers"].select().decodeList<ProviderDto>()
            Result.success(dtos.map { mapProviderDto(it) })
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
            categoryName = dto.category, // You might want to map this properly or join
            rating = dto.rating,
            reviewCount = dto.review_count,
            distanceKm = dto.service_radius?.toDouble() ?: 2.0, // Calculate locally based on coordinates
            startingPrice = dto.starting_price,
            isVerified = dto.is_verified,
            isEmergencyAvailable = dto.is_available,
            yearsExperience = dto.experience_years,
            bio = dto.description,
            locationAddress = dto.location_address ?: "",
            phone = dto.phone,
            servicesOffered = emptyList(), // Fetch via join later
            reviews = emptyList() // Fetch via join later
        )
    }
}