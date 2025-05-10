package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.R
import kotlinx.coroutines.delay

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onGuestContinue: () -> Unit
) {
    var selectedRole by remember { mutableStateOf("student") }
    var campusId by remember { mutableStateOf("") }
    var lastVerifiedCampusId by remember { mutableStateOf("") }
    var lastVerifiedRole by remember { mutableStateOf("") }

    var isCampusIdVerified by remember { mutableStateOf(false) }
    var isVerifyButtonEnabled by remember { mutableStateOf(true) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authResult by authViewModel.authResult.observeAsState()
    val verifyResult by authViewModel.campusIdVerifyResult.observeAsState(null)
    var verifyErrorMessage by remember { mutableStateOf<String?>(null) }

    val emailVerificationSent by authViewModel.emailVerificationSent.observeAsState()
    val emailVerifiedResult by authViewModel.emailVerified.observeAsState()

    val isEmailVerifyButtonEnabled by authViewModel.isEmailVerifyButtonEnabled.observeAsState(true)
    val pollingCompleted by authViewModel.isPollingCompleted.observeAsState(false)

    var manualCheckRequested by remember { mutableStateOf(false) }


    // 🔁 Automatically reset verification if ID or role changes
    LaunchedEffect(campusId, selectedRole) {
        if (campusId != lastVerifiedCampusId || selectedRole != lastVerifiedRole) {
            isCampusIdVerified = false
            isVerifyButtonEnabled = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onGuestContinue) {
                Text("Continue as Guest", color = Color.Blue)
            }
        }

        OutlinedTextField(
            value = campusId,
            onValueChange = {
                campusId = it
                if (isCampusIdVerified) {
                    isCampusIdVerified = false
                    isVerifyButtonEnabled = true
                    lastVerifiedCampusId = ""
                }
            },
            label = {
                if (selectedRole == "student") Text("Enter Your University Roll No.")
                else if (selectedRole == "faculty") Text("Enter Your Faculty ID")
                else Text("Enter Admin Password")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("student", "faculty", "admin").forEach { role ->
                Button(
                    onClick = {
                        if (role != selectedRole) {
                            selectedRole = role
                            if (isCampusIdVerified) {
                                isCampusIdVerified = false
                                isVerifyButtonEnabled = true
                                lastVerifiedRole = ""
                            }
                        }
                    },
                    enabled = selectedRole != role
                ) {
                    Text(role)
                }
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    authViewModel.verifyCampusId(role = selectedRole, campusId = campusId)
                }
            },
            enabled = isVerifyButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Verify")
        }

        verifyResult?.let { result ->
            LaunchedEffect(result) {
                when (result) {
                    is Result.Success -> {
                        isCampusIdVerified = true
                        isVerifyButtonEnabled = false
                        lastVerifiedCampusId = campusId
                        lastVerifiedRole = selectedRole

                    }

                    is Result.Error -> {
                        isCampusIdVerified = false
                        isVerifyButtonEnabled = true
                        verifyErrorMessage = result.exception.message
                        Toast.makeText(
                            context,
                            "Verification Failed: ${result.exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                // Clear the result so it doesn't interfere with recomposition
                authViewModel.clearVerifyResult()
            }
        }

        if (isCampusIdVerified) {
            Text(
                "ID verified for $selectedRole",
                modifier = Modifier.padding(8.dp),
                color = colorResource(R.color.verify_green)
            )
        } else if (verifyErrorMessage != null) {
            Text(
                "Verification Failed: $verifyErrorMessage",
                modifier = Modifier.padding(8.dp),
                color = colorResource(R.color.verify_red)
            )
        }

        // Only show sign-up form if verified
        if (isCampusIdVerified) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        authViewModel.onEmailChanged()
                    },
                    label = { Text("Email") },
                    modifier = Modifier.weight(2.8f)
                )
                Spacer(modifier = Modifier.padding(start = 4.dp))
                Button(
                    onClick = { authViewModel.sendEmailVerification(email) },
                    enabled = isEmailVerifyButtonEnabled,
                    modifier = Modifier.weight(1.2f)
                ) {
                    Text("Verify")
                }
            }

            // Show status or prompt to check inbox for email verification
            emailVerificationSent?.let {
                when (it) {
                    is Result.Success -> Text("Verification email sent on ${email.substringBefore("@")} confirm to proceed", color = Color.Green)
                    is Result.Error -> Text("${it.exception.message}" , color = Color.Red)
                    else -> {}
                }
            }

            //Checking whether the email verified or not
            when (val result = emailVerifiedResult) {
                is Result.Success -> {
                    if (result.data) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Button(onClick = {
                            authViewModel.finalizeSignUp(
                                email,
                                password,
                                firstName,
                                lastName,
                                selectedRole,
                                campusId
                            )
                        }) {
                            Text("Finish Registration")
                        }
                    }else if (pollingCompleted) {
                        // If email is not verified and polling has ended
                        Text("Email not verified yet.")
                        Button(onClick = {
                            manualCheckRequested = true
                            authViewModel.checkEmailVerifiedManually()
                        }) {
                            Text("Done")
                        }
                    }
                }
                is Result.Error -> {
                    if (pollingCompleted) {
                        Text("Check Your Inbox to Verify the Email")
                        Button(onClick = {
                            manualCheckRequested = true
                            authViewModel.checkEmailVerifiedManually()
                        }) {
                            Text("Done")
                        }
                    }
                }
                null -> {
                    if (pollingCompleted) {
                        Text("Email verification not confirmed.")
                        Button(onClick = {
                            manualCheckRequested = true
                            authViewModel.checkEmailVerifiedManually()
                        }) {
                            Text("Done")
                        }
                    }
                } // no result yet
            }

            Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = "Already have an account? Sign in.",
                color = Color.Blue,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )

            authResult?.let { result ->
                LaunchedEffect(result) {
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(
                                context,
                                "Registration Successful. Login to explore",
                                Toast.LENGTH_LONG
                            ).show()
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
}