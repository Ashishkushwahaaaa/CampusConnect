package com.ashish.campusconnect

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashish.campusconnect.viewmodel.AuthViewModel
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.data.SessionManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel(),onNavigateToSignUp: ()-> Unit,onSignInSuccess: () -> Unit, onNavigateToHome: () -> Unit){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authResult by authViewModel.authResult.observeAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            //Add navigation to home screen to see only posts
            coroutineScope.launch {
                sessionManager.setGuestMode(true)
                // Navigate to Home Screen
                onNavigateToHome()
            }

        }){
            Text("Continue as Guest")
        }
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Email")},
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {Text("Password")},
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {Text(text = "Login") }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Don't have an Account? Sign Up", modifier = Modifier.clickable{
            // Navigate to SignUp Screen
            onNavigateToSignUp()
        })

        authResult?.let { result ->
            LaunchedEffect(result) {
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            sessionManager.setLoggedInUser()
                        }
                        onSignInSuccess()
                    }
                    is Result.Error -> {
                        Toast.makeText(context,"Login Failed: ${result.exception.message}",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}