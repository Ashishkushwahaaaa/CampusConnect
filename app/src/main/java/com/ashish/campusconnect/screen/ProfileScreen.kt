package com.ashish.campusconnect.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.ashish.campusconnect.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

//@Composable
//fun ProfileScreen(
//    onGuestLogin: () -> Unit,
//    onLogoutRequest: () -> Unit
//) {
//    val user = FirebaseAuth.getInstance().currentUser
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Top
//    ) {
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Profile Image
//        val photoUrl = user?.photoUrl?.toString()
//        val painter = rememberAsyncImagePainter(photoUrl)
//
//        Image(
//            painter = painter,
//            contentDescription = "Profile Image",
//            modifier = Modifier
//                .size(100.dp)
//                .padding(8.dp)
//                .then(Modifier),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = user?.displayName ?: "Guest User",
//            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text(
//            text = user?.email ?: "Not Logged In",
//            style = MaterialTheme.typography.bodyMedium,
//            color = Color.Gray
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Logout Button
//        if (user != null) {
//            Button(
//                onClick =  onLogoutRequest,
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text(text = "Logout", color = Color.White)
//            }
//        } else {
//            Text("Login to access profile features", color = Color.Gray)
//            Button(
//                onClick = onGuestLogin,
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text(text = "Login", color = Color.White)
//            }
//        }
//    }
//}


@Composable
fun ProfileScreen(
    padding: PaddingValues,
    onGuestLogin: () -> Unit,
    onLogoutRequest: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email

    val userData by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(email) {
        if (email != null) {
            viewModel.loadUserProfile(email)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        if (userData != null) {
            Text(text = "Name: ${userData!!.firstName} ${userData!!.lastName}")
            Text(text = "Email: ${userData!!.email}")
            Text(text = "Role: ${userData!!.role}")
            Text(text = "Campus ID: ${userData!!.campusId}")
        } else if (error != null) {
            Text(text = "Error: $error", color = Color.Red)
        } else {
            if(email != null){
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (firebaseUser != null) {
            Button(
                onClick = onLogoutRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Logout", color = Color.White)
            }
        } else {
            Text("Login to access profile features", color = Color.Gray)
            Button(
                onClick = onGuestLogin,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Login", color = Color.White)
            }
        }
    }
}
