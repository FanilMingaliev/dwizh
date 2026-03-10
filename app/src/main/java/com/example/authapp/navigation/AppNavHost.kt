package com.example.authapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.authapp.data.auth.FakeAuthRepository
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.profile.ProfileRepository
import com.example.authapp.ui.auth.NameEntryScreen
import com.example.authapp.ui.auth.BirthDateScreen
import com.example.authapp.ui.auth.GenderScreen
import com.example.authapp.ui.auth.EmailAuthScreen
import com.example.authapp.ui.auth.PhoneAuthScreen
import com.example.authapp.ui.events.CreateEventScreen
import com.example.authapp.ui.events.CreateEventViewModel
import com.example.authapp.ui.events.EventsScreen
import com.example.authapp.ui.events.EventsViewModel
import com.example.authapp.ui.login.LoginScreen
import com.example.authapp.ui.login.LoginViewModel
import com.example.authapp.ui.profile.ProfileScreen
import com.example.authapp.ui.profile.ProfileViewModel
import com.example.authapp.ui.profile.EditProfileScreen
import com.example.authapp.ui.profile.AboutMeScreen
import com.example.authapp.ui.profile.FactsListScreen
import com.example.authapp.ui.profile.FactEditScreen
import com.example.authapp.ui.register.RegisterScreen
import com.example.authapp.ui.register.RegisterType
import com.example.authapp.ui.register.RegisterViewModel

object RootRoutes {
    const val Auth = "auth"
    const val Main = "main"
}

object AuthRoutes {
    const val Login = "login"
    const val EmailAuth = "auth/email"
    const val PhoneAuth = "auth/phone"
    const val NameEntry = "auth/name"
    const val BirthDate = "auth/birthdate"
    const val Gender = "auth/gender"
    const val Register = "register/{type}"

    fun registerRoute(type: String): String = "register/$type"
}

object MainRoutes {
    const val Events = "events"
    const val Profile = "profile"
    const val CreateEvent = "events/create"
    const val EditProfile = "profile/edit"
    const val AboutMe = "profile/about"
    const val FactsList = "profile/facts"
    const val FactEdit = "profile/facts/{index}"

    fun factEditRoute(index: Int): String = "profile/facts/$index"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val authRepository = remember { FakeAuthRepository() }
    val eventsRepository = remember { EventsRepository() }
    val profileRepository = remember { ProfileRepository() }

    val loginViewModel = remember { LoginViewModel(authRepository) }
    val eventsViewModel = remember { EventsViewModel(eventsRepository) }
    val profileViewModel = remember { ProfileViewModel(profileRepository) }

    NavHost(
        navController = navController,
        startDestination = RootRoutes.Auth
    ) {
        navigation(
            startDestination = AuthRoutes.Login,
            route = RootRoutes.Auth
        ) {
            composable(AuthRoutes.Login) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(RootRoutes.Main) {
                            popUpTo(RootRoutes.Auth) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onRegisterEmailClick = {
                        navController.navigate(AuthRoutes.EmailAuth)
                    },
                    onRegisterPhoneClick = {
                        navController.navigate(AuthRoutes.PhoneAuth)
                    }
                )
            }
            composable(AuthRoutes.EmailAuth) {
                EmailAuthScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onOtherMethod = {
                        navController.navigate(AuthRoutes.Login) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onAuthSuccess = { email ->
                        authRepository.setCurrentUser(email)
                        navController.navigate(AuthRoutes.NameEntry)
                    }
                )
            }
            composable(AuthRoutes.PhoneAuth) {
                PhoneAuthScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onOtherMethod = {
                        navController.navigate(AuthRoutes.Login) {
                            popUpTo(AuthRoutes.Login) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onAuthSuccess = { phone ->
                        authRepository.setCurrentUser(phone)
                        navController.navigate(AuthRoutes.NameEntry)
                    }
                )
            }
            composable(AuthRoutes.NameEntry) {
                NameEntryScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onContinue = { fullName ->
                        profileRepository.updateProfile(profileRepository.profile.value.copy(fullName = fullName))
                        navController.navigate(AuthRoutes.BirthDate)
                    }
                )
            }
            composable(AuthRoutes.BirthDate) {
                BirthDateScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onContinue = { birthDate ->
                        profileRepository.updateProfile(profileRepository.profile.value.copy(birthDate = birthDate))
                        navController.navigate(AuthRoutes.Gender)
                    }
                )
            }
            composable(AuthRoutes.Gender) {
                GenderScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onContinue = { gender ->
                        profileRepository.updateProfile(profileRepository.profile.value.copy(gender = gender))
                        navController.navigate(RootRoutes.Main) {
                            popUpTo(RootRoutes.Auth) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = AuthRoutes.Register,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { entry ->
                val registerType = RegisterType.from(entry.arguments?.getString("type"))
                val registerViewModel = remember(registerType) {
                    RegisterViewModel(authRepository, registerType)
                }
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onRegisterSuccess = {
                        navController.navigate(RootRoutes.Main) {
                            popUpTo(RootRoutes.Auth) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        navigation(
            startDestination = MainRoutes.Events,
            route = RootRoutes.Main
        ) {
            composable(MainRoutes.Events) {
                MainScaffold(navController = navController, showBottomBar = true) {
                    EventsScreen(
                        viewModel = eventsViewModel,
                        onAddEvent = { navController.navigate(MainRoutes.CreateEvent) }
                    )
                }
            }
            composable(MainRoutes.Profile) {
                MainScaffold(navController = navController, showBottomBar = true) {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onEditProfile = { navController.navigate(MainRoutes.EditProfile) },
                        onLogout = {
                            authRepository.logout()
                            navController.navigate(RootRoutes.Auth) {
                                popUpTo(RootRoutes.Main) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            composable(MainRoutes.EditProfile) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    EditProfileScreen(
                        onNavigateBack = { navController.navigateUp() },
                        onAboutMe = { navController.navigate(MainRoutes.AboutMe) },
                        onFacts = { navController.navigate(MainRoutes.FactsList) }
                    )
                }
            }
            composable(MainRoutes.AboutMe) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    AboutMeScreen(
                        viewModel = profileViewModel,
                        onClose = { navController.navigateUp() }
                    )
                }
            }
            composable(MainRoutes.FactsList) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    FactsListScreen(
                        onSelectFact = { index -> navController.navigate(MainRoutes.factEditRoute(index)) },
                        onClose = { navController.navigateUp() }
                    )
                }
            }
            composable(
                route = MainRoutes.FactEdit,
                arguments = listOf(navArgument("index") { type = NavType.IntType })
            ) { entry ->
                val index = entry.arguments?.getInt("index") ?: 0
                MainScaffold(navController = navController, showBottomBar = false) {
                    FactEditScreen(
                        index = index,
                        viewModel = profileViewModel,
                        onClose = { navController.navigateUp() }
                    )
                }
            }
            composable(MainRoutes.CreateEvent) {
                val createEventViewModel = remember { CreateEventViewModel(eventsRepository) }
                MainScaffold(navController = navController, showBottomBar = false) {
                    CreateEventScreen(
                        viewModel = createEventViewModel,
                        onSaveSuccess = { navController.popBackStack() },
                        onCancel = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MainScaffold(
    navController: NavHostController,
    showBottomBar: Boolean,
    content: @Composable () -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == MainRoutes.Events,
                        onClick = {
                            navController.navigate(MainRoutes.Events) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { androidx.compose.material3.Icon(Icons.Default.Home, contentDescription = "Events") },
                        label = { Text("Events") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == MainRoutes.CreateEvent,
                        onClick = {
                            navController.navigate(MainRoutes.CreateEvent) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = { androidx.compose.material3.Icon(Icons.Default.Add, contentDescription = "Add") },
                        label = { Text("Add") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { androidx.compose.material3.Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorites") },
                        label = { Text("Likes") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { androidx.compose.material3.Icon(Icons.Default.Email, contentDescription = "Chats") },
                        label = { Text("Chats") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == MainRoutes.Profile,
                        onClick = {
                            navController.navigate(MainRoutes.Profile) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { androidx.compose.material3.Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            content()
        }
    }
}
