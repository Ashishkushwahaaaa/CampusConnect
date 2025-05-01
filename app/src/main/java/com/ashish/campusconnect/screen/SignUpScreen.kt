package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.ashish.campusconnect.data.Result

@Composable
fun SignUpScreen(authViewModel: AuthViewModel = viewModel(),onNavigateToLogin: () -> Unit, onSignUpSuccess: () -> Unit, onNavigateToHome: () -> Unit){
    var email by remember { mutableStateOf("") }
    var password by remember{mutableStateOf("")}
    var firstName by remember{mutableStateOf("")}
    var lastName by remember{mutableStateOf("")}
    var campusId by remember{mutableStateOf("")}

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
            // Add navigation to home screen to see only posts
            coroutineScope.launch {
                sessionManager.setGuestMode(true)
                // Navigate to Home Screen
                onNavigateToHome()
            }
        }) {Text("Continue as Guest") }

        OutlinedTextField(
            value = firstName,
            onValueChange = {firstName = it},
            label = {Text(text = "FirstName")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = {lastName = it},
            label = {Text(text = "LastName")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        OutlinedTextField(
            value = campusId,
            onValueChange = {campusId = it},
            label = {Text(text = "Campus ID")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text(text = "Email")},
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {Text(text = "Password")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        Button(
            onClick = {
                // Add signUp function
                authViewModel.signUp(email, password, firstName, lastName, campusId)
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {Text(text = "Sign Up") }
        Spacer(modifier = Modifier.padding(16.dp))
        Text(text = "Already have an account? Sign in.",
            modifier = Modifier.clickable{onNavigateToLogin()}
        )

        authResult?.let { result ->
            LaunchedEffect(result) {
                when (result) {
                    is Result.Success -> {
                        Toast.makeText(
                            context,
                            "RegistrationSuccessful, Login to explore",
                            Toast.LENGTH_LONG
                        ).show()
                        email = ""
                        password = ""
                        firstName = ""
                        lastName = ""
                        campusId = ""

                        onSignUpSuccess()
                    }

                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            "Sign Up Failed: ${result.exception.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview(){
//    SignUpScreen({})
}