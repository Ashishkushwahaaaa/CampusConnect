package com.ashish.campusconnect.screen

import android.app.Activity
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

//This composable may be used in future, to set the status bar color specific to the respective screen
@Composable
fun SetStatusBarColor(
    color: Color,
    useDarkIcons: Boolean
) {
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        window.statusBarColor = color.toArgb()
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.isAppearanceLightStatusBars = useDarkIcons
    }
}

//How to call the above screen from any screen, for specific status bar appearance
//@Composable
//fun ExampleScreen() {
//
////    Method 1:
//    val background = MaterialTheme.colorScheme.background
//    val isDark = isSystemInDarkTheme()
//    // In dark mode: background might be dark, so use light icons
//    SetStatusBarColor(color = background, useDarkIcons = !isDark)
//
////    Method 2:
//    //OR Directly like this if not using dynamic color
//    SetStatusBarColor(color = Color(0xFF4B4A4A), useDarkIcons = false)
//
//    //Content of the screen
//    Text("Example Screen")
//}