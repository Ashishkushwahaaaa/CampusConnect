package com.ashish.campusconnect.data

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.vector.ImageVector

data class Contact(
    val method: String,
    val value: String,
    val icon: ImageVector
)