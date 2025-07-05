package com.ashish.campusconnect.data

import androidx.compose.ui.graphics.vector.ImageVector

data class DrawerItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
//    val onClick: () -> Unit
)