package com.example.ca4localpro.data.auth

import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole
import kotlinx.coroutines.delay

/**
 * Temporary authentication is enabled for development only.
 * It must be replaced by real backend authentication before production release.
 */
class TemporaryAuthDataSource : AuthDataSource {

    private data class DevUser(
        val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val role: UserRole,
        val isVerified: Boolean
    )

    private val devCustomer = DevUser(
        id = "dev_customer_001",
        name = "Demo Customer",
        email = "customer@localpro.dev",
        phone = "+91 98765 43210",
        role = UserRole.CUSTOMER,
        isVerified = true
    )

    private val devProvider = DevUser(
        id = "dev_provider_001",
        name = "Demo Provider",
        email = "provider@localpro.dev",
        phone = "+91 98765 43211",
        role = UserRole.PROVIDER,
        isVerified = true
    )

    private val inMemoryUsers = mutableListOf(devCustomer, devProvider)

    override suspend fun authenticate(email: String, password: String): Result<UserProfile> {
        delay(600) // Simulate network delay

        val cleanEmail = email.trim()
        
        // Match dev accounts with hardcoded passwords, or any other in-memory user with a generic password check
        val matchedUser = inMemoryUsers.find { it.email == cleanEmail }
        
        if (matchedUser == null) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }
        
        val isValidPassword = when (matchedUser.email) {
            devCustomer.email -> password == "Customer@123"
            devProvider.email -> password == "Provider@123"
            else -> password.isNotEmpty() // Accept any password for newly registered in-memory users for testing
        }

        if (!isValidPassword) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        return Result.success(
            UserProfile(
                id = matchedUser.id,
                name = matchedUser.name,
                email = matchedUser.email,
                phone = matchedUser.phone,
                role = matchedUser.role,
                primaryAddress = "Demo Address, Dev City"
            )
        )
    }

    override suspend fun register(name: String, email: String, phone: String, password: String, role: UserRole): Result<UserProfile> {
        delay(600)
        
        val cleanEmail = email.trim()
        if (inMemoryUsers.any { it.email == cleanEmail }) {
            return Result.failure(IllegalArgumentException("User with this email already exists"))
        }
        
        val newUser = DevUser(
            id = "dev_user_${System.currentTimeMillis()}",
            name = name,
            email = cleanEmail,
            phone = phone,
            role = role,
            isVerified = true
        )
        
        inMemoryUsers.add(newUser)
        
        return Result.success(
            UserProfile(
                id = newUser.id,
                name = newUser.name,
                email = newUser.email,
                phone = newUser.phone,
                role = newUser.role,
                primaryAddress = ""
            )
        )
    }

    override suspend fun checkSession(): Result<UserProfile?> {
        // Not used directly if we handle session in Repository/Manager, 
        // but returning null to signify backend doesn't have a session.
        return Result.success(null)
    }

    override suspend fun logout(): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }
}