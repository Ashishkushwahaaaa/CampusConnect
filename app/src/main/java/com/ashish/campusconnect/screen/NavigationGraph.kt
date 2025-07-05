package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.data.User
import com.ashish.campusconnect.viewmodel.PostDetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun MainNavGraph(mainNavController: NavHostController) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = null)
    val coroutineScope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser

    NavHost(navController = mainNavController, startDestination = Screen.SplashScreen.route) {
        composable(Screen.SplashScreen.route) {
            if (isGuest != null) {
                SplashScreen(onNavigate = {
                    coroutineScope.launch {
                        if (isGuest == true) {
                            mainNavController.navigate(Screen.MainScreen.route) {
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

                                mainNavController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            } else if (currentUser.isEmailVerified) {
                                // Fully registered user
                                mainNavController.navigate(Screen.MainScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            } else {
                                // Not verified, not incomplete — ask to register
                                mainNavController.navigate(Screen.SignUpScreen.route) {
                                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                }
                            }
                        } else {
                            mainNavController.navigate(Screen.SignUpScreen.route) {
                                popUpTo(Screen.SplashScreen.route) { inclusive = true }
                            }
                        }
                    }
                })
            } else {
                SplashScreen(onNavigate = {})
            }
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    mainNavController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                },
                onSignUpSuccess = {
                    mainNavController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                },
                onGuestContinue = {
                    mainNavController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.SignUpScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.LoginScreen.route) {
            LoginScreen(
                onNavigateToSignUp = {
                    mainNavController.navigate(Screen.SignUpScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                onSignInSuccess = {
                    mainNavController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                onGuestContinue = {
                    mainNavController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                }
            )

        }
        composable(Screen.MainScreen.route) {
            val screenNavController = rememberNavController()
            MainScreen(
                screenNavController = screenNavController,
                mainNavController = mainNavController,
                onGuestLogin = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(true)
                        mainNavController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = true }
                        }
                    }
                },
                onUserLogout = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(true) // switching to GuestMode, you can set it as false to behave as new user
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        mainNavController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = true }
                        }
                    }
                },
                onCreatePostClick = { screenNavController.navigate(Screen.PostScreen.route) },
            )
        }
    }
}

@Composable
fun ScreenNavGraph(
    screenNavController: NavHostController,
    mainNavController: NavHostController,
    padding: PaddingValues,
    sessionManager: SessionManager,
    coroutineScope: CoroutineScope,
)
{
    val context = LocalContext.current

    NavHost(
        navController = screenNavController,
        startDestination = Screen.Update.route
    )
    {
        composable(Screen.Home.route) {
            HomeScreen(padding = padding)
        }
        composable(Screen.Update.route){
            UpdateScreen(
                padding = padding,
                onPostClick = { post ->
                    screenNavController.navigate("Post Detail/${post.id}")
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                padding = padding,
                onGuestLogin = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(true)
                        mainNavController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = true }

                        }
                    }
                },
                onLogoutRequest = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(true) // switching to GuestMode, you can set it as null and do required changes to set it as new user
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                        mainNavController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.MainScreen.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Community.route) {
            CommunityScreen(padding = padding)
        }
        composable(Screen.PostScreen.route){
            PostScreen(
                padding = padding,
                onPostUploaded = { screenNavController.navigate(Screen.Update.route){
                    popUpTo(Screen.PostScreen.route){inclusive = true}
                } }
            )
        }
        composable("Post Detail/{postId}") { backStackEntry ->
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
                    PostDetailsScreen(padding = padding, post = post!!)
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Post not found", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        // Drawer Bar Options Navigation
        composable(Screen.Setting.route) { SettingsScreen(padding = padding) }
        composable(Screen.About.route) { AboutScreen(padding = padding) }
    }
}