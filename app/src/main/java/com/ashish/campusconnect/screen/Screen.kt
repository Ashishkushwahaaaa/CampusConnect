package com.ashish.campusconnect.screen

sealed class Screen (val route: String){
    object SplashScreen: Screen("splash_screen")
    object SignUpScreen: Screen("signup_screen")
    object LoginScreen: Screen("login_screen")
    object HomeScreen: Screen("home_screen")
    object PostScreen: Screen("post_screen")
    object PostDetailsScreen: Screen("post_details_screen")
}