package com.ashish.campusconnect.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

@Composable
fun CommunityScreen(padding: PaddingValues) {
    Text("Community Screen",modifier = Modifier.padding(padding), style = MaterialTheme.typography.bodyLarge)
}