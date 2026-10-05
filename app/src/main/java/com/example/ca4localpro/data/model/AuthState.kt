package com.example.ca4localpro.data.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class AuthState {
    object CheckingSession : AuthState()
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val user: UserProfile) : AuthState()
    data class AuthenticationError(val message: String) : AuthState()
}
