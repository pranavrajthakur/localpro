package com.example.ca4localpro.data.local

import android.content.Context
import com.example.ca4localpro.data.model.UserProfile
import com.example.ca4localpro.data.model.UserRole

class AuthSessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("localpro_secure_session", Context.MODE_PRIVATE)

    fun saveSession(user: UserProfile, token: String = "session_token_lp_101") {
        prefs.edit()
            .putString("key_user_id", user.id)
            .putString("key_user_name", user.name)
            .putString("key_user_email", user.email)
            .putString("key_user_phone", user.phone)
            .putString("key_user_role", user.role.name)
            .putString("key_user_address", user.primaryAddress)
            .putString("key_session_token", token)
            .putBoolean("key_is_logged_in", true)
            .apply()
    }

    fun getSessionUser(): UserProfile? {
        val isLoggedIn = prefs.getBoolean("key_is_logged_in", false)
        if (!isLoggedIn) return null

        val id = prefs.getString("key_user_id", null) ?: return null
        val name = prefs.getString("key_user_name", "") ?: ""
        val email = prefs.getString("key_user_email", "") ?: ""
        val phone = prefs.getString("key_user_phone", "") ?: ""
        val roleStr = prefs.getString("key_user_role", UserRole.CUSTOMER.name) ?: UserRole.CUSTOMER.name
        val address = prefs.getString("key_user_address", "") ?: ""

        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.CUSTOMER
        }

        return UserProfile(
            id = id,
            name = name,
            email = email,
            phone = phone,
            role = role,
            primaryAddress = address
        )
    }

    fun hasValidSession(): Boolean {
        return prefs.getBoolean("key_is_logged_in", false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
