package com.ashish.campusconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.ashish.campusconnect.screen.MainNavGraph
import com.ashish.campusconnect.ui.theme.CampusConnectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val mainNavController = rememberNavController()
            CampusConnectTheme {
                //Navigation Graph
                MainNavGraph(mainNavController = mainNavController)
            }
        }
    }
}