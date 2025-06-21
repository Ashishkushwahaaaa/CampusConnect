package com.ashish.campusconnect.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.data.BottomNavItem
import com.ashish.campusconnect.data.SessionManager
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onGuestLogin: () -> Unit,
    onUserLogout: () -> Unit,
    ) {
    val bottomNavController = rememberNavController()
    var selectedItem by remember { mutableStateOf(1) }

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = false)
    var showLogoutDialog by remember{mutableStateOf(false)}

    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, "main_home"),
        BottomNavItem("Events", Icons.Default.DateRange, "main_events"),
        BottomNavItem("Profile", Icons.Default.AccountCircle, "main_profile"),
        BottomNavItem("Source", Icons.Default.AutoStories, "main_courses")
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // CC Logo
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
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Title
                        Text(
                            text = "Campus Connect",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF6D6D),
                    titleContentColor = colorResource(id = R.color.black)
                ),
                modifier = Modifier.wrapContentHeight(),
                actions = {
                    if (isGuest){
                        IconButton(onClick = { onGuestLogin() }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Login")
                        }
                    }else{
                        IconButton(onClick = {showLogoutDialog = true}) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Logout")
                        }
                    }
                }
            )
        },
        containerColor = colorScheme.background,
        floatingActionButton =  {
            if(!isGuest && selectedItem == 1){
                FloatingActionButton(onClick = onCreatePostClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post"
                    )
                }
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
            onLogout = onUserLogout,
            padding = padding,
            onPostClick = onPostClick,
            onCreatePostClick = onCreatePostClick,
            onGuestLogin = onGuestLogin,
            onLogoutRequest = { showLogoutDialog = true }
        )
    }
}