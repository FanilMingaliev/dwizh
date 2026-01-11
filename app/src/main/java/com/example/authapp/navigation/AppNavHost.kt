package com.example.authapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authapp.data.auth.FakeAuthRepository
import com.example.authapp.ui.home.HomeScreen
import com.example.authapp.ui.login.LoginScreen
import com.example.authapp.ui.login.LoginViewModel

object Routes {
    const val Login = "login"
    const val Home = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val authRepository = remember { FakeAuthRepository() }
    val loginViewModel = remember { LoginViewModel(authRepository) }

    NavHost(
        navController = navController,
        startDestination = Routes.Login
    ) {
        composable(Routes.Login) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.Home) {
            HomeScreen(
                email = loginViewModel.uiState.value.email,
                onLogout = {
                    loginViewModel.clearSession()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
