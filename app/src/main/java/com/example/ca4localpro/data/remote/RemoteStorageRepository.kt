package com.example.ca4localpro.data.remote

import io.github.jan.supabase.storage.storage

class RemoteStorageRepository {
    
    private val supabase = SupabaseClientProvider.client

    /**
     * Uploads a profile image for a user.
     * Note: Supabase Storage bucket 'profiles' must be created and public.
     * Currently only stubs the architecture since no image picker is added to the UI yet.
     */
    suspend fun uploadProfileImage(userId: String, imageBytes: ByteArray): Result<String> {
        return try {
            val bucket = supabase.storage.from("profiles")
            val fileName = "$userId/profile_${System.currentTimeMillis()}.jpg"
            bucket.upload(fileName, imageBytes)
            
            val url = bucket.publicUrl(fileName)
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}