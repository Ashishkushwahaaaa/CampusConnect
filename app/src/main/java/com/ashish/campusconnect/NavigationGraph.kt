package com.ashish.campusconnect

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashish.campusconnect.viewmodel.HomeViewModel

@Composable
fun NavigationGraph(
    modifier: Modifier,
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    startDestination: String)
{
    NavHost(navController = navController, startDestination = startDestination){
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
                    navController.navigate("post_details_screen/${post.id}")
                },
                onCreatePostClick = { navController.navigate(Screen.PostScreen.route) }
            )
        }

        composable(Screen.PostScreen.route){
            PostScreen(
                onPostUploaded = { navController.navigate(Screen.HomeScreen.route) }
            )
        }

        composable("post_details_screen/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            postId?.let {
                val post = homeViewModel.getPostById(it) // Fetch post by ID
                post?.let { PostDetailsScreen(post = it) }
            }
        }

    }
}