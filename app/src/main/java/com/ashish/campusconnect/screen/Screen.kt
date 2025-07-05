package com.ashish.campusconnect.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object SplashScreen : Screen("Splash")
    object LoginScreen : Screen("Login")
    object SignUpScreen : Screen("SignUp")
    object MainScreen : Screen("Main")
    object Home : Screen("Home")
    object Update : Screen("Update")
    object Profile : Screen("Profile")
    object Community : Screen("Community")
    object Setting : Screen("Setting")
    object About : Screen("About")
    object PostScreen: Screen("Post")
    object PostDetailsScreen: Screen("Post Detail")
}