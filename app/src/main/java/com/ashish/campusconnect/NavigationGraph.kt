package com.ashish.campusconnect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationGraph(modifier: Modifier,navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.SignUpScreen.route){
        composable(Screen.SignUpScreen.route){
            SignUpScreen(onNavigateToLogin = {
                navController.navigate(Screen.LoginScreen.route)
            })
        }
        composable(Screen.LoginScreen.route){
            LoginScreen(onNavigateToSignUp = {
                navController.navigate(Screen.SignUpScreen.route)
            }){
                navController.navigate(Screen.HomeScreen.route)
            }
        }

    }
}