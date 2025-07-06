package com.ashish.campusconnect.screen

import android.R.attr.divider
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.areStatusBarsVisible
import androidx.compose.foundation.layout.areSystemBarsVisible
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.data.BottomNavItem
import com.ashish.campusconnect.data.DrawerItem
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.screen.UpdateScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.LogoDev
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Dp.Companion.Hairline
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.navigation.NavController

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
//    SetStatusBarAppearance()

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

    ModalNavigationDrawer(
        scrimColor = Color.Black.copy(alpha = 0.5f), //The amount of blurred background when the navigation drawer is opened
        drawerContent = {
            DrawerContent(drawerItems, screenNavController,isGuest,onGuestLogin,onUserLogout) {
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
                                text = currentRoute?.substringBefore("/")?: "CampusConnect",
                                color = Color(0xFFFF6D6D),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.surface,
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
                        contentColor = Color(0xFFFF6D6D),
                        containerColor = colorScheme.onSurface
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
    onGuestLogin: () -> Unit,
    onUserLogout: () -> Unit,
    closeDrawer: () -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier
            .width(280.dp), //Remove height to bring back to default drawer height
        drawerContainerColor = colorScheme.surface,
        drawerShape = RoundedCornerShape(bottomEnd = 30.dp),
//        windowInsets = WindowInsets.statusBars
    ){
        //Header
        Column(
            modifier = Modifier
                .background(Color(0xFFFF6D6D))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
//                CCLogo() //Need to fix this to use previously made logo, size is the problem for now
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CC",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CampusConnect",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isGuest) "@ Guest User" else "Welcome Back!",
                fontSize = 14.sp,
                color = Color.White
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
                    selectedContainerColor = Color.Transparent,
                    selectedTextColor = Color(0xFFFF6D6D),
                    selectedIconColor = Color(0xFFFF6D6D),
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = Color.Black,
                    unselectedIconColor = Color.Black
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
        shadowElevation = 12.dp,
//        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
    ) {
        Box {
            NavigationBar(
                containerColor = colorScheme.background,
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
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color(0xFFFF6D6D),
                            unselectedIconColor = colorScheme.onBackground.copy(alpha = 0.7f),
                            selectedTextColor = Color(0xFFFF6D6D),
                            unselectedTextColor = colorScheme.onBackground.copy(alpha = 0.7f),
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
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .zIndex(1f), //make sure always on the top and visible
                color = Color(0xFFFF6D6D),
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
    SideEffect {
        val window = activity?.window
        window?.statusBarColor = Color(0xFF4B4A4A).toArgb() // Match drawer header color
        WindowCompat.getInsetsController(window, window?.decorView).isAppearanceLightStatusBars = false
    }
}