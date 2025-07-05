package com.ashish.campusconnect.screen

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


//@Composable
//fun CampusConnectTopBar() {
//    val themeColor = Color(0xFFFF6D6D)
//    val height = 180.dp
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height)
//    ) {
//        Canvas(modifier = Modifier.matchParentSize()) {
//            val width = size.width
//            val heightPx = size.height
//
//            val waveHeight = heightPx * 0.25f
//
//            val path = Path().apply {
//                moveTo(0f, 0f)
//                lineTo(0f, heightPx - waveHeight)
//
//                quadraticBezierTo(
//                    width * 0.25f, heightPx,
//                    width * 0.5f, heightPx - waveHeight
//                )
//                quadraticBezierTo(
//                    width * 0.75f, heightPx - 2 * waveHeight,
//                    width, heightPx - waveHeight
//                )
//
//                lineTo(width, 0f)
//                close()
//            }
//
//            drawPath(path, color = themeColor)
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 20.dp, vertical = 40.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // CC Logo
//            Box(
//                modifier = Modifier
//                    .size(48.dp)
//                    .clip(CircleShape)
//                    .background(Color.White.copy(alpha = 0.3f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "CC",
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Title
//            Text(
//                text = "Campus Connect",
//                color = Color.White,
//                fontSize = 22.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//    }
//}



@Composable
fun CampusConnectTopBar(
    isGuest: Boolean,
    onGuestLogin: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val themeColor = Color(0xFFFF6D6D)
    val height = 180.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val heightPx = size.height
            val waveHeight = heightPx * 0.25f

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, heightPx - waveHeight)

                quadraticBezierTo(
                    width * 0.25f, heightPx,
                    width * 0.5f, heightPx - waveHeight
                )
                quadraticBezierTo(
                    width * 0.75f, heightPx - 2 * waveHeight,
                    width, heightPx - waveHeight
                )

                lineTo(width, 0f)
                close()
            }

            drawPath(path, color = themeColor)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 40.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // CC Logo
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CC",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title
                Text(
                    text = "Campus Connect",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Actions
            IconButton(onClick = {
                if (isGuest) onGuestLogin() else onLogoutClick()
            }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = if (isGuest) "Login" else "Logout",
                    tint = Color.White
                )
            }
        }
    }
}


//****************************************************************************
//TopAppBar with animation

//@Composable
//fun CampusConnectTopBar(
//    isGuest: Boolean,
//    onGuestLogin: () -> Unit,
//    onLogoutClick: () -> Unit
//) {
//    val themeColor = Color(0xFFFF6D6D)
//    val height = 180.dp
//
//    // This animates the wave shift
//    val waveOffset = remember { Animatable(0f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            waveOffset.animateTo(
//                targetValue = 1f,
//                animationSpec = infiniteRepeatable(
//                    animation = tween(durationMillis = 10000, easing = LinearEasing),
//                    repeatMode = RepeatMode.Reverse
//                )
//            )
//            waveOffset.snapTo(0f)
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(height)
//    ) {
//        Canvas(modifier = Modifier.matchParentSize()) {
//            val width = size.width
//            val heightPx = size.height
//            val waveHeight = heightPx * 0.25f
//
//            val animatedShift = waveOffset.value * width
//
//            val path = Path().apply {
//                moveTo(0f, 0f)
//                lineTo(0f, heightPx - waveHeight)
//
//                quadraticBezierTo(
//                    width * 0.25f + animatedShift, heightPx,
//                    width * 0.5f + animatedShift, heightPx - waveHeight
//                )
//                quadraticBezierTo(
//                    width * 0.75f + animatedShift, heightPx - 2 * waveHeight,
//                    width + animatedShift, heightPx - waveHeight
//                )
//
//                lineTo(width, 0f)
//                close()
//            }
//
//            drawPath(path, color = themeColor)
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 20.dp, top = 40.dp, end = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(CircleShape)
//                        .background(Color.White.copy(alpha = 0.3f)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = "CC",
//                        color = Color.White,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(12.dp))
//
//                Text(
//                    text = "Campus Connect",
//                    color = Color.White,
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//
//            IconButton(onClick = {
//                if (isGuest) onGuestLogin() else onLogoutClick()
//            }) {
//                Icon(
//                    imageVector = Icons.Default.AccountCircle,
//                    contentDescription = if (isGuest) "Login" else "Logout",
//                    tint = Color.White
//                )
//            }
//        }
//    }
//}



//*********************************************************************************

//Change the current Scaffold by this Scaffold if gonna use this TopAppBar

//Scaffold(
//topBar = {
//    CampusConnectTopBar(
//        isGuest = isGuest,
//        onGuestLogin = onGuestLogin,
//        onLogoutClick = { showLogoutDialog = true }
//    )
//},
//containerColor = colorResource(id = R.color.background_container_color_2),
//floatingActionButton = {
//    if (!isGuest) {
//        FloatingActionButton(onClick = onCreatePostClick) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Create Post"
//            )
//        }
//    }
//}
//)