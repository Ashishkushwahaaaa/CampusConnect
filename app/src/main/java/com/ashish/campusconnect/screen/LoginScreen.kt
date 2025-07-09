package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashish.campusconnect.viewmodel.AuthViewModel
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToSignUp: ()-> Unit,
    onSignInSuccess: () -> Unit,
    onGuestContinue: () -> Unit
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authResult by authViewModel.authResult.observeAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val resetResult by authViewModel.passwordResetResult.observeAsState()

    if (showResetDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your registered email:")
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        placeholder = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.resetPassword(resetEmail)
                }) {
                    Text("Send Reset Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        WavyTopBar(
            selectedScreen = AuthScreen.SIGN_IN,
            onSignInClick = {}, // Already on this screen
            onSignUpClick = { onNavigateToSignUp() }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            sessionManager.setGuestMode(true)
                            // Navigate to Home Screen
                            onGuestContinue()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Continue as Guest", style = Typography.bodyLarge)
                }
            }
            Card(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                border = BorderStroke(1.dp, color = colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email*") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = "Email")}
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password*") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    leadingIcon = {Icon(imageVector = Icons.Default.Key, contentDescription = "Password")},
                    trailingIcon = {
                        val visibilityIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    }
                )
                Button(
                    onClick = { authViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) { Text(text = "Login") }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ){
                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            resetEmail = ""
                            authViewModel.clearPasswordResetResult()
                            showResetDialog = true
                        }
                )

            }

            Text(
                text = "Don't have an account? Sign Up.",
//                color = Color.Blue,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(16.dp).clickable { onNavigateToSignUp() }
            )

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
                            if(email.isBlank() || password.isBlank()){
                                Toast.makeText(context,"Please fill all the mandatory fields", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(context,"Login Failed: ${result.exception.message}",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            resetResult?.let { result ->
                LaunchedEffect(result) {
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(context, "Reset link sent to $resetEmail", Toast.LENGTH_LONG).show()
                            showResetDialog = false
                        }
                        is Result.Error -> {
                            Toast.makeText(context, "Failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }
    }
}