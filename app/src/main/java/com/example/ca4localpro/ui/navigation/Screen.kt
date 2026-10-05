package com.example.ca4localpro.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main") {
        fun createRoute(tab: String = "home") = "main?tab=$tab"
    }
    object Categories : Screen("categories_flow")
    object ProviderList : Screen("provider_list?category={category}&query={query}") {
        fun createRoute(category: String = "", query: String = "") =
            "provider_list?category=$category&query=$query"
    }
    object ProviderDetails : Screen("provider_details/{providerId}") {
        fun createRoute(providerId: String) = "provider_details/$providerId"
    }
}
