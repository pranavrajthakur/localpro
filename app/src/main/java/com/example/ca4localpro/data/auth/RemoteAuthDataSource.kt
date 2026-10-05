package com.example.ca4localpro.data.auth

import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole
import com.example.ca4localpro.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: String,
    val profile_image: String? = null,
    val primary_address: String? = null
)

class RemoteAuthDataSource : AuthDataSource {

    private val supabase = SupabaseClientProvider.client

    override suspend fun authenticate(email: String, password: String): Result<UserProfile> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            
            val userId = supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("Authentication succeeded but user ID is missing.")

            // Fetch profile data
            val profile = supabase.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingle<ProfileDto>()

            val role = try {
                UserRole.valueOf(profile.role.uppercase())
            } catch (e: Exception) {
                UserRole.CUSTOMER
            }

            Result.success(
                UserProfile(
                    id = profile.id,
                    name = profile.name,
                    email = profile.email,
                    phone = profile.phone ?: "",
                    role = role,
                    primaryAddress = profile.primary_address ?: "Set your address in profile",
                    avatarUrl = profile.profile_image
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, phone: String, password: String, role: UserRole): Result<UserProfile> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            
            // Note: After sign up, depending on Supabase settings, email verification might be required.
            // We return a dummy success if it succeeds. Real app should handle user creation in DB
            // usually via a PostgreSQL trigger on auth.users -> profiles.
            
            Result.success(
                UserProfile(
                    id = "temp_id", 
                    name = name, 
                    email = email, 
                    phone = phone, 
                    role = role
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun checkSession(): Result<UserProfile?> {
        return try {
            val session = supabase.auth.currentSessionOrNull()
            if (session != null) {
                val userId = session.user?.id ?: return Result.success(null)
                
                val profile = supabase.postgrest["profiles"]
                    .select { filter { eq("id", userId) } }
                    .decodeSingle<ProfileDto>()

                val role = try {
                    UserRole.valueOf(profile.role.uppercase())
                } catch (e: Exception) {
                    UserRole.CUSTOMER
                }

                Result.success(
                    UserProfile(
                        id = profile.id,
                        name = profile.name,
                        email = profile.email,
                        phone = profile.phone ?: "",
                        role = role,
                        primaryAddress = profile.primary_address ?: "",
                        avatarUrl = profile.profile_image
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}