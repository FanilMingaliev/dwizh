package com.example.authapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.authapp.R
import com.example.authapp.data.chats.ChatsRepository
import com.example.authapp.ui.theme.DvizhColors
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.authapp.data.auth.FirebaseAuthRepository
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.profile.ProfileRepository
import com.example.authapp.ui.auth.NameEntryScreen
import com.example.authapp.ui.auth.BirthDateScreen
import com.example.authapp.ui.auth.GenderScreen
import com.example.authapp.ui.auth.EmailAuthScreen
import com.example.authapp.ui.auth.PhoneAuthScreen
import com.example.authapp.ui.chats.ChatDetailScreen
import com.example.authapp.ui.chats.ChatsListScreen
import com.example.authapp.ui.chats.ChatsViewModel
import com.example.authapp.ui.events.EventsCalendarScreen
import com.example.authapp.ui.events.CreateEventPromotionScreen
import com.example.authapp.ui.events.CreateEventScreen
import com.example.authapp.ui.events.CreateEventViewModel
import com.example.authapp.ui.events.EventDetailScreen
import com.example.authapp.ui.events.EventParticipantsScreen
import com.example.authapp.ui.events.EventsScreen
import com.example.authapp.ui.events.EventsViewModel
import com.example.authapp.ui.myevents.MyMovesScreen
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
    const val EventsCalendar = "events/calendar"
    const val MyMoves = "my_moves"
    const val Chats = "chats"
    const val ChatDetail = "chats/thread/{chatId}"

    fun chatDetailRoute(chatId: String): String = "chats/thread/$chatId"
    const val Profile = "profile"
    const val CreateEvent = "events/create"
    const val CreateEventPromotion = "events/create/promotion"
    const val EventDetail = "event/{eventId}"
    const val EventParticipants = "event/{eventId}/participants"

    fun eventDetailRoute(eventId: String): String = "event/$eventId"
    fun eventParticipantsRoute(eventId: String): String = "event/$eventId/participants"
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
    val authRepository = remember { FirebaseAuthRepository() }
    val eventsRepository = remember { EventsRepository() }
    val chatsRepository = remember { ChatsRepository() }
    val profileRepository = remember { ProfileRepository() }

    val loginViewModel = remember { LoginViewModel(authRepository) }
    val eventsViewModel = remember { EventsViewModel(eventsRepository, chatsRepository) }
    val chatsViewModel = remember { ChatsViewModel(chatsRepository) }
    val createEventViewModel = remember { CreateEventViewModel(eventsRepository) }
    val profileViewModel = remember { ProfileViewModel(profileRepository) }
    val currentUser by authRepository.currentUser.collectAsState()
    val startDestination = if (currentUser != null) RootRoutes.Main else RootRoutes.Auth

    NavHost(
        navController = navController,
        startDestination = startDestination
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
                        navController.navigate(AuthRoutes.NameEntry) {
                            popUpTo(AuthRoutes.Login) { inclusive = false }
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
            composable(MainRoutes.EventsCalendar) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    val events by eventsViewModel.events.collectAsState()
                    EventsCalendarScreen(
                        events = events,
                        onPickDate = { d ->
                            eventsViewModel.requestFeedJumpToDate(d)
                            navController.popBackStack()
                        },
                        onClose = { navController.navigateUp() }
                    )
                }
            }
            composable(MainRoutes.Events) {
                MainScaffold(navController = navController, showBottomBar = true) {
                    EventsScreen(
                        viewModel = eventsViewModel,
                        onAddEvent = { navController.navigate(MainRoutes.CreateEvent) },
                        onOpenEvent = { event ->
                            navController.navigate(MainRoutes.eventDetailRoute(event.id))
                        },
                        onOpenCalendar = { navController.navigate(MainRoutes.EventsCalendar) }
                    )
                }
            }
            composable(
                route = MainRoutes.EventParticipants,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { entry ->
                val eventId = entry.arguments?.getString("eventId") ?: return@composable
                MainScaffold(navController = navController, showBottomBar = false) {
                    EventParticipantsScreen(
                        eventId = eventId,
                        viewModel = eventsViewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
            composable(
                route = MainRoutes.EventDetail,
                arguments = listOf(navArgument("eventId") { type = NavType.StringType })
            ) { entry ->
                val eventId = entry.arguments?.getString("eventId") ?: return@composable
                MainScaffold(navController = navController, showBottomBar = false) {
                    EventDetailScreen(
                        eventId = eventId,
                        viewModel = eventsViewModel,
                        onNavigateBack = { navController.navigateUp() },
                        onRegister = {
                            eventsViewModel.registerForEventById(eventId)
                        },
                        onOpenParticipants = {
                            navController.navigate(MainRoutes.eventParticipantsRoute(eventId))
                        }
                    )
                }
            }
            composable(MainRoutes.MyMoves) {
                MainScaffold(navController = navController, showBottomBar = true) {
                    val events by eventsViewModel.events.collectAsState()
                    val organizerUid = currentUser
                    val mine = remember(events, organizerUid) {
                        if (organizerUid.isNullOrBlank()) emptyList()
                        else events.filter { it.organizerId == organizerUid }
                    }
                    MyMovesScreen(
                        myEvents = mine,
                        onCreateMove = {
                            navController.navigate(MainRoutes.CreateEvent) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        onOpenEvent = { ev ->
                            navController.navigate(MainRoutes.eventDetailRoute(ev.id))
                        }
                    )
                }
            }
            composable(
                route = MainRoutes.ChatDetail,
                arguments = listOf(navArgument("chatId") { type = NavType.StringType })
            ) { entry ->
                val chatId = entry.arguments?.getString("chatId") ?: return@composable
                MainScaffold(navController = navController, showBottomBar = false) {
                    val list by chatsViewModel.chatList.collectAsState()
                    val title = remember(chatId, list) {
                        list.find { it.chatId == chatId }?.title ?: "Чат"
                    }
                    ChatDetailScreen(
                        chatId = chatId,
                        title = title,
                        viewModel = chatsViewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
            composable(MainRoutes.Chats) {
                MainScaffold(navController = navController, showBottomBar = true) {
                    ChatsListScreen(
                        viewModel = chatsViewModel,
                        onOpenChat = { row ->
                            navController.navigate(MainRoutes.chatDetailRoute(row.chatId))
                        }
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
            composable(MainRoutes.CreateEventPromotion) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    CreateEventPromotionScreen(
                        viewModel = createEventViewModel,
                        onPublished = {
                            createEventViewModel.reset()
                            navController.popBackStack(MainRoutes.Events, inclusive = false)
                        },
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
            composable(MainRoutes.CreateEvent) {
                MainScaffold(navController = navController, showBottomBar = false) {
                    CreateEventScreen(
                        viewModel = createEventViewModel,
                        onContinueToPromotion = {
                            navController.navigate(MainRoutes.CreateEventPromotion)
                        },
                        onCancel = {
                            createEventViewModel.reset()
                            navController.popBackStack()
                        }
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
                val barColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DvizhColors.Brand,
                    selectedTextColor = DvizhColors.Brand,
                    indicatorColor = DvizhColors.Brand.copy(alpha = 0.12f),
                    unselectedIconColor = DvizhColors.Slate600,
                    unselectedTextColor = DvizhColors.Slate600
                )
                NavigationBar {
                    val eventsSel = currentRoute == MainRoutes.Events ||
                        currentRoute == MainRoutes.EventsCalendar
                    NavigationBarItem(
                        selected = eventsSel,
                        onClick = {
                            navController.navigate(MainRoutes.Events) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_feed),
                                contentDescription = "Лента"
                            )
                        },
                        label = { Text("Лента") },
                        colors = barColors
                    )
                    val mySel = currentRoute == MainRoutes.MyMoves
                    NavigationBarItem(
                        selected = mySel,
                        onClick = {
                            navController.navigate(MainRoutes.MyMoves) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_my),
                                contentDescription = "Мои движы"
                            )
                        },
                        label = { Text("Мои") },
                        colors = barColors
                    )
                    val createSel = currentRoute == MainRoutes.CreateEvent ||
                        currentRoute == MainRoutes.CreateEventPromotion
                    NavigationBarItem(
                        selected = createSel,
                        onClick = {
                            navController.navigate(MainRoutes.CreateEvent) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_add),
                                contentDescription = "Создать"
                            )
                        },
                        label = { Text("Создать") },
                        colors = barColors
                    )
                    val chatsSel = currentRoute == MainRoutes.Chats ||
                        (currentRoute?.startsWith("chats/thread") == true)
                    NavigationBarItem(
                        selected = chatsSel,
                        onClick = {
                            navController.navigate(MainRoutes.Chats) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_chats),
                                contentDescription = "Чаты"
                            )
                        },
                        label = { Text("Чаты") },
                        colors = barColors
                    )
                    val profileSel = currentRoute == MainRoutes.Profile
                    NavigationBarItem(
                        selected = profileSel,
                        onClick = {
                            navController.navigate(MainRoutes.Profile) {
                                popUpTo(MainRoutes.Events) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_nav_profile),
                                contentDescription = "Профиль"
                            )
                        },
                        label = { Text("Профиль") },
                        colors = barColors
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
