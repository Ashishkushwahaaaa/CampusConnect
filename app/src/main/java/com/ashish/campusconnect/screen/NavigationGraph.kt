package com.ashish.campusconnect.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.viewmodel.PostDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun NavigationGraph(modifier: Modifier, navController: NavHostController){

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route){

        composable(Screen.SplashScreen.route){
            if (isGuest != null) {
                SplashScreen(onNavigate = {
                    coroutineScope.launch {
                        if (isGuest == true) {
                            navController.navigate(Screen.UpdateScreen.route) {
                                popUpTo(Screen.SplashScreen.route) { inclusive = true }
                            }
                        } else if (currentUser != null) {
                            currentUser.reload().await()

                            val uid = currentUser.uid
                            val snapshot = FirebaseFirestore.getInstance()
                                .collection("incompleteSignups")
                                .document(uid)
                                .get()
                                .await()

                            if (snapshot.exists()) {
                                // Incomplete user, delete account and navigate to SignUp
                                FirebaseAuth.getInstance().currentUser?.delete()
                                FirebaseFirestore.getInstance().collection("incompleteSignups")
                                    .document(uid).delete()

                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            } else if (currentUser.isEmailVerified) {
                                // Fully registered user
                                navController.navigate(Screen.UpdateScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            } else {
                                // Not verified, not incomplete — ask to register
                                navController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            }
                        } else {
                            navController.navigate(Screen.SignUpScreen.route) {
                                popUpTo(Screen.SplashScreen.route) { inclusive = true }
                            }
                        }
                    }
                })
            } else {
                SplashScreen(onNavigate = {})
            }
        }
        composable(Screen.SignUpScreen.route){
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.LoginScreen.route) {
                        //Here i am not using popUpTo() because it was creating confusion as the
                        // first screen was LoginScreen and also the current screen was login and
                        // hence functionality was not as expectedly working.So manually popped up last two screen from the stack
//                        navController.popBackStack()
//                        navController.popBackStack()
                        popUpTo(Screen.SignUpScreen.route){inclusive = true}
                    }
                },
                onGuestContinue = {
                    navController.navigate(Screen.UpdateScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                onNavigateToSignUp = { navController.navigate(Screen.SignUpScreen.route){
                    popUpTo(Screen.LoginScreen.route){inclusive = true}
                } },
                onSignInSuccess = {
                    navController.navigate(Screen.UpdateScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                onGuestContinue = {
                    navController.navigate(Screen.UpdateScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                }
            )

        }

        composable(Screen.UpdateScreen.route){
            UpdateScreen(
                onPostClick = { post ->
                    navController.navigate("post_details_screen/${post.id}")
                },
                onCreatePostClick = { navController.navigate(Screen.PostScreen.route) },
                onGuestLogin = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(false)
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.UpdateScreen.route) { inclusive = true }
                        }
                    }
                },
                onUserLogout = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(false) // switching to GuestMode, you can set it as false to behave as new user
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.UpdateScreen.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.PostScreen.route){
            PostScreen(
                onPostUploaded = { navController.navigate(Screen.UpdateScreen.route){
                    popUpTo(Screen.PostScreen.route){inclusive = true}
                } }
            )
        }

        composable("post_details_screen/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            val viewModel: PostDetailsViewModel = viewModel()
            val post by viewModel.post.collectAsState()
            val loading by viewModel.loading.collectAsState()

            LaunchedEffect(postId) {
                viewModel.loadPost(postId)
            }
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                post != null -> {
                    PostDetailsScreen(post = post!!)
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Post not found", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}