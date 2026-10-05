package com.example.ca4localpro.data.auth

import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole

interface AuthDataSource {
    suspend fun authenticate(email: String, password: String): Result<UserProfile>
    suspend fun register(name: String, email: String, phone: String, password: String, role: UserRole): Result<UserProfile>
    suspend fun checkSession(): Result<UserProfile?>
    suspend fun logout(): Result<Unit>
}
