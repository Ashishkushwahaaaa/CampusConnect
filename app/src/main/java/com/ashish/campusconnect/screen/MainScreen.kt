package com.ashish.campusconnect.screen

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ashish.campusconnect.data.BottomNavItem
import com.ashish.campusconnect.data.DrawerItem
import com.ashish.campusconnect.data.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import com.ashish.campusconnect.ui.theme.AppThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    screenNavController: NavHostController,
    mainNavController: NavHostController,
    onGuestLogin: () -> Unit,
    onUserLogout: () -> Unit,
    onCreatePostClick: () -> Unit,
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = false)
    var showLogoutDialog by remember{mutableStateOf(false)}
    val currentRoute = screenNavController.currentBackStackEntryAsState().value?.destination?.route
    val drawerOpenTrigger = remember { mutableStateOf(0) } //To animate the drawer logo

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Update.route,
        Screen.Profile.route,
        Screen.Community.route
    )

    val drawerItems = listOf(
        DrawerItem(
            label = "Settings",
            route = Screen.Setting.route,
            icon = Icons.Default.Settings
        ),
        DrawerItem(
            label = "About",
            route = Screen.About.route,
            icon = Icons.Default.Info
        ),
        DrawerItem(
            label = if(isGuest) "Login" else "Logout",
            route = Screen.LoginScreen.route,
            icon = Icons.AutoMirrored.Filled.ExitToApp,
        )
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onUserLogout()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            drawerOpenTrigger.value++ // triggers CCLogo animation
        }
    }


//    SetStatusBarAppearance()

    ModalNavigationDrawer(
        scrimColor = Color.Black.copy(alpha = 0.5f), //The amount of blurred background when the navigation drawer is opened
        drawerContent = {
            DrawerContent(drawerItems, screenNavController,isGuest, drawerOpenTrigger.value, onGuestLogin,onUserLogout) {
                coroutineScope.launch { drawerState.close() }
            }
        },
        drawerState = drawerState,
    ) {
        if (drawerState.isOpen) {
            BackHandler {
                coroutineScope.launch {
                    drawerState.close()
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = currentRoute?.substringBefore("/") ?: "CampusConnect",
                            color = AppThemeColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.wrapContentHeight(),
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (isGuest){
                            IconButton(onClick = { onGuestLogin() }) {
                                Icon(Icons.AutoMirrored.Default.Login, contentDescription = "Login")
                            }
                        }else{
                            IconButton(onClick = {showLogoutDialog = true}) {
                                Icon(Icons.AutoMirrored.Default.Logout, contentDescription = "Logout")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (currentRoute in bottomBarRoutes) {
                    BottomNavigationBar(screenNavController)
                }
            },
            floatingActionButton =  {
                if(!isGuest && currentRoute == Screen.Update.route){
                    FloatingActionButton(
                        onClick = onCreatePostClick,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            imageVector = Icons.Default.BorderColor,
                            contentDescription = "Create Post",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            },
        ) { innerPadding ->
            //Navigation Graph for bottomNav and DrawerNav Screens
            ScreenNavGraph(
                screenNavController = screenNavController,
                mainNavController = mainNavController,
                padding = innerPadding,
                sessionManager = sessionManager,
                coroutineScope = coroutineScope,
            )
        }
    }
}

@Composable
fun DrawerContent(
    drawerItems: List<DrawerItem>,
    screenNavController: NavHostController,
    isGuest:Boolean,
    drawerOpenTrigger: Any,
    onGuestLogin: () -> Unit,
    onUserLogout: () -> Unit,
    closeDrawer: () -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp), //Remove height to bring back to default drawer height
        drawerContainerColor = colorScheme.surface,
        drawerShape = RoundedCornerShape(bottomEnd = 30.dp),
//        windowInsets = WindowInsets.statusBars
    ){
        //Header
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                CCLogo(2.8f,triggerKey = drawerOpenTrigger) // This logo method is Defined in SplashScreen
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CampusConnect",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isGuest) "@ Guest User" else "Welcome Back!",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        val currentRoute = screenNavController.currentBackStackEntryAsState().value?.destination?.route

        drawerItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                icon = { Icon(item.icon, contentDescription = item.label) },
                onClick = {
                    closeDrawer()

                    if (item.route == Screen.LoginScreen.route) {
                        if (isGuest) onGuestLogin() else onUserLogout()
                        return@NavigationDrawerItem
                    }
                    // Navigate and clear everything above the main screen
                    screenNavController.navigate(item.route) {
                        popUpTo(Screen.Update.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }


    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
        BottomNavItem("Update", Screen.Update.route, Icons.Default.CheckCircle),
        BottomNavItem("Community", Screen.Community.route, Icons.Default.Groups),
        BottomNavItem("Profile", Screen.Profile.route, Icons.Default.Face),
    )
    Surface (
        shadowElevation = 4.dp,
        modifier = Modifier.background(colorScheme.surface)
    ) {
        Box {
            NavigationBar(
            ) {
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { item ->

                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Update.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(26.dp),
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        ),
                        label = {
                            Text(
                                item.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                    )
                }
            }

            // Divider Code After Navigation? - Drawn on the top of the Navigation Bar(child defined next in the parent is drawn on the top and then the first one is drawn below it)
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f), //make sure always on the top and visible
                color = colorScheme.primary,
                thickness = Hairline
            )
        }
    }
}
@Composable
fun SettingsScreen(padding: PaddingValues) {
    Text("Settings Screen", modifier = Modifier.padding(padding), style = MaterialTheme.typography.bodyLarge)
}
@Composable
fun AboutScreen(padding: PaddingValues) {
    Text("About Screen",modifier = Modifier.padding(padding), style = MaterialTheme.typography.bodyLarge)
}

@Composable
fun SetStatusBarAppearance() {
    val activity = LocalActivity.current
    val backgroundColor = colorScheme.surface
    val isDarkTheme = isSystemInDarkTheme()
    SideEffect {
        val window = activity?.window
        window?.statusBarColor = backgroundColor.toArgb() // Match drawer header color
        WindowCompat.getInsetsController(window, window?.decorView)
            .isAppearanceLightStatusBars = !isDarkTheme
    }
}