package com.ashish.campusconnect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashish.campusconnect.data.Post

@Composable
fun NavigationGraph(modifier: Modifier,navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.SignUpScreen.route){
        composable(Screen.SignUpScreen.route){
            SignUpScreen(
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) },
                onSignUpSuccess = { navController.navigate(Screen.LoginScreen.route) },
                onNavigateToHome = { navController.navigate(Screen.HomeScreen.route) }
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUpScreen.route) },
                onSignInSuccess = {navController.navigate(Screen.HomeScreen.route)},
                onNavigateToHome = {navController.navigate(Screen.HomeScreen.route)}
            )

        }
        composable(Screen.HomeScreen.route){
            HomeScreen(
                onPostClick = { post ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("post", post)
                    navController.navigate("details")
                },
                onCreatePostClick = { navController.navigate(Screen.PostScreen.route) }
            )
        }

        composable(Screen.PostScreen.route){
            PostScreen(
                onPostUploaded = { navController.navigate(Screen.HomeScreen.route) }
            )
        }

        composable(Screen.PostDetailsScreen.route) {
            val post = navController.previousBackStackEntry?.savedStateHandle?.get<Post>("post")
            post?.let {
                PostDetailsScreen(post = it)
            }
        }
    }
}