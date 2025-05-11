package com.ashish.campusconnect.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

class BottomNavItem {
    sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
        object Home : BottomNavItem("Home", Icons.Default.Home, "home")
        object Profile : BottomNavItem("Profile", Icons.Default.AccountCircle, "profile")
        object Settings : BottomNavItem("Events", Icons.Default.DateRange, "event")
    }

}