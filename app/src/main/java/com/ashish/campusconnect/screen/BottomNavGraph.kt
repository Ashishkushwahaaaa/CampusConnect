
package com.ashish.campusconnect.screen

import android.util.MutableBoolean
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.data.SessionManager

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    onLogout: () -> Unit,
    padding: PaddingValues,
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onGuestLogin: () -> Unit,
    onLogoutRequest: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main_events"
    ) {
        composable("main_home") {
            homescreen()
        }

        composable("main_events") {
            //Event_screen()
            UpdateScreen(
//                navController = navController,
                onPostClick = onPostClick,
                onCreatePostClick = onCreatePostClick,
                onGuestLogin = onGuestLogin,
                onUserLogout = onLogout,
//                padding = padding
            )
        }

        composable("main_profile") {
            ProfileScreen(
                onGuestLogin = onGuestLogin,
                onLogoutRequest = onLogoutRequest,
            )
        }

        composable("main_courses"){
            courses()
        }
    }
}
