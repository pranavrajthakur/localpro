package com.example.ca4localpro.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ca4localpro.ui.components.LocalProBottomBar
import com.example.ca4localpro.ui.screens.auth.LoginScreen
import com.example.ca4localpro.ui.screens.auth.RegisterScreen
import com.example.ca4localpro.ui.screens.bookings.BookingsScreen
import com.example.ca4localpro.ui.screens.categories.CategoriesScreen
import com.example.ca4localpro.ui.screens.home.HomeScreen
import com.example.ca4localpro.ui.screens.profile.ProfileScreen
import com.example.ca4localpro.ui.screens.provider.ProviderDetailsScreen
import com.example.ca4localpro.ui.screens.provider.ProviderListScreen
import com.example.ca4localpro.ui.screens.splash.SplashScreen
import com.example.ca4localpro.viewmodel.AuthViewModel
import com.example.ca4localpro.viewmodel.HomeViewModel
import com.example.ca4localpro.viewmodel.ProviderViewModel

@Composable
fun LocalProNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    providerViewModel: ProviderViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val authState by authViewModel.authState.collectAsState()
            SplashScreen(
                onSplashFinished = {
                    if (authState is com.example.ca4localpro.data.model.AuthState.Authenticated) {
                        val user = (authState as com.example.ca4localpro.data.model.AuthState.Authenticated).user
                        val tab = if (user.role == com.example.ca4localpro.data.model.UserRole.PROVIDER) "provider_home" else "home"
                        navController.navigate(Screen.Main.createRoute(tab)) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    val user = authViewModel.currentUser.value
                    val tab = if (user?.role == com.example.ca4localpro.data.model.UserRole.PROVIDER) "provider_home" else "home"
                    navController.navigate(Screen.Main.createRoute(tab)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onBrowseAsGuest = {
                    navController.navigate(Screen.Main.createRoute("home")) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    val user = authViewModel.currentUser.value
                    val tab = if (user?.role == com.example.ca4localpro.data.model.UserRole.PROVIDER) "provider_home" else "home"
                    navController.navigate(Screen.Main.createRoute(tab)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "main?tab={tab}",
            arguments = listOf(navArgument("tab") { defaultValue = "home" })
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab") ?: "home"
            var currentTab by remember { mutableStateOf(tab) }
            val currentUser by authViewModel.currentUser.collectAsState()
            val isProvider = currentUser?.role == com.example.ca4localpro.data.model.UserRole.PROVIDER
            val defaultHomeTab = if (isProvider) "provider_home" else "home"

            // Intercept system Back button when in Explore, Bookings or Profile tab
            BackHandler(enabled = currentTab != defaultHomeTab) {
                currentTab = defaultHomeTab
            }

            Scaffold(
                bottomBar = {
                    LocalProBottomBar(
                        selectedRoute = currentTab,
                        onRouteSelected = { newRoute ->
                            currentTab = newRoute
                        },
                        isProvider = isProvider
                    )
                }
            ) { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                when (currentTab) {
                    "provider_home" -> {
                        val providerDashboardViewModel: com.example.ca4localpro.viewmodel.ProviderDashboardViewModel = viewModel()
                        val currentUser by authViewModel.currentUser.collectAsState()
                        
                        LaunchedEffect(currentUser?.id) {
                            currentUser?.id?.let {
                                providerDashboardViewModel.loadProviderProfile(it)
                            }
                        }
                        
                        com.example.ca4localpro.ui.screens.provider.ProviderDashboardScreen(
                            viewModel = providerDashboardViewModel,
                            onLogout = {
                                authViewModel.logout(onSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                })
                            },
                            modifier = modifier
                        )
                    }
                    "home" -> HomeScreen(
                        viewModel = homeViewModel,
                        onCategoryClick = { categoryId ->
                            navController.navigate(Screen.ProviderList.createRoute(category = categoryId))
                        },
                        onProviderClick = { providerId ->
                            navController.navigate(Screen.ProviderDetails.createRoute(providerId = providerId))
                        },
                        onSeeAllCategoriesClick = {
                            navController.navigate(Screen.Categories.route)
                        },
                        onSeeAllProvidersClick = {
                            navController.navigate(Screen.ProviderList.createRoute())
                        },
                        onEmergencyClick = {
                            navController.navigate(Screen.ProviderList.createRoute(query = "emergency"))
                        },
                        onMyBookingsClick = {
                            currentTab = "bookings"
                        },
                        onProfileClick = {
                            currentTab = "profile"
                        },
                        modifier = modifier
                    )
                    "explore" -> CategoriesScreen(
                        viewModel = homeViewModel,
                        onCategoryClick = { categoryId ->
                            navController.navigate(Screen.ProviderList.createRoute(category = categoryId))
                        },
                        onBackClick = {
                            currentTab = defaultHomeTab
                        },
                        modifier = modifier
                    )
                    "bookings" -> BookingsScreen(
                        onProviderClick = { providerId ->
                            navController.navigate(Screen.ProviderDetails.createRoute(providerId = providerId))
                        },
                        onBackClick = {
                            currentTab = defaultHomeTab
                        },
                        modifier = modifier
                    )
                    "profile" -> {
                        val profileViewModel: com.example.ca4localpro.viewmodel.ProfileViewModel = viewModel()
                        val currentUser by authViewModel.currentUser.collectAsState()
                        
                        LaunchedEffect(currentUser) {
                            profileViewModel.loadProfile(currentUser)
                        }
                        
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onLogout = {
                                authViewModel.logout(onSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                })
                            },
                            modifier = modifier
                        )
                    }
                }
            }
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                viewModel = homeViewModel,
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.ProviderList.createRoute(category = categoryId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ProviderList.route,
            arguments = listOf(
                navArgument("category") { defaultValue = "" },
                navArgument("query") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            val query = backStackEntry.arguments?.getString("query") ?: ""

            ProviderListScreen(
                viewModel = providerViewModel,
                initialCategory = category,
                initialQuery = query,
                onProviderClick = { providerId ->
                    navController.navigate(Screen.ProviderDetails.createRoute(providerId = providerId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ProviderDetails.route,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId") ?: ""

            ProviderDetailsScreen(
                viewModel = providerViewModel,
                providerId = providerId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
