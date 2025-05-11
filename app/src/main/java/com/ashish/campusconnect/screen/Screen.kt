package com.ashish.campusconnect.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen (val route: String){
    object SplashScreen: Screen("splash_screen")
    object SignUpScreen: Screen("signup_screen")
    object LoginScreen: Screen("login_screen")
    object HomeScreen: Screen("home_screen")
    object PostScreen: Screen("post_screen")
    object PostDetailsScreen: Screen("post_details_screen")
    object CampusIdVerificationScreen: Screen("campus_id_verification_screen")
}

