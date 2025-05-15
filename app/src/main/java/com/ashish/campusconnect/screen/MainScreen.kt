// MainScreen.kt
package com.ashish.campusconnect.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import java.lang.reflect.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onGuestLogin: () -> Unit,
    onUserLogout: () -> Unit,
    sessionManager: SessionManager,



    ) {
    val bottomNavController = rememberNavController()
    var selectedItem by remember { mutableStateOf(0) }

    data class BottomNavItem(
        val title: String,
        val icon: ImageVector,
        val route: String
    )

    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home,Screen.HomeScreen.route),
        BottomNavItem("Events", Icons.Default.DateRange,Screen.Event.route),
        BottomNavItem("Profile", Icons.Default.AccountCircle,Screen.Profile.route)
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Connect") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.top_bar_color),
                    titleContentColor = colorResource(id = R.color.black)
                ),
                actions = {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user == null) {
                        IconButton(onClick = { onGuestLogin() }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Login")
                        }
                    } else {
                        IconButton(onClick = { onUserLogout() }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Logout")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePostClick) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Post")
            }
        },
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        BottomNavGraph(
            navController = bottomNavController,
            sessionManager = sessionManager,
            onLogout = onUserLogout,
            padding = padding,
            onPostClick = onPostClick,
            onCreatePostClick = onCreatePostClick,
            onGuestLogin = onGuestLogin
        )
    }
}
