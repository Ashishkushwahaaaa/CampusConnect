package com.ashish.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.ui.theme.CampusConnectTheme
import com.ashish.campusconnect.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        lifecycleScope.launch {
            val isGuest = sessionManager.isGuest.first() // Read once
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

            val startDestination = if (isGuest || isLoggedIn) {
                Screen.HomeScreen.route
            } else {
                Screen.SignUpScreen.route
            }

            setContent {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = viewModel()
                CampusConnectTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavigationGraph(
                            modifier = Modifier.padding(innerPadding),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}