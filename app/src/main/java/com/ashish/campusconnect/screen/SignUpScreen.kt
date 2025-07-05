package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
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
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashish.campusconnect.data.SessionManager
import com.ashish.campusconnect.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.ui.theme.AppThemeColor
import com.ashish.campusconnect.ui.theme.Pink40
import com.ashish.campusconnect.ui.theme.Typography

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
    val sessionManager = remember { SessionManager(context) }

    val authResult by authViewModel.authResult.observeAsState()
    val verifyResult by authViewModel.campusIdVerifyResult.observeAsState(null)
    var verifyErrorMessage by remember { mutableStateOf<String?>(null) }

    val emailVerificationSent by authViewModel.emailVerificationSent.observeAsState()
    val emailVerifiedResult by authViewModel.emailVerified.observeAsState()

    val isEmailVerifyButtonEnabled by authViewModel.isEmailVerifyButtonEnabled.observeAsState(true)
    val pollingCompleted by authViewModel.isPollingCompleted.observeAsState(false)

    var manualCheckRequested by remember { mutableStateOf(false) }
    var emailVerifiedFlag by remember{mutableStateOf(false)}
    var passwordVisible by remember { mutableStateOf(false) }
    val isEmaiVerifyEnabled = isEmailVerifyButtonEnabled && firstName.isNotBlank() && email.isNotBlank()

    // 🔁 Automatically reset verification if ID or role changes
    LaunchedEffect(campusId, selectedRole) {
        if (campusId != lastVerifiedCampusId || selectedRole != lastVerifiedRole) {
            isCampusIdVerified = false
            isVerifyButtonEnabled = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colorScheme.background)) {
        WavyTopBar(
            selectedScreen = AuthScreen.SIGN_UP,
            onSignInClick = { onNavigateToLogin() },
            onSignUpClick = {} // Already on this screen
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    coroutineScope.launch {
                        sessionManager.setGuestMode(true)
                        // Navigate to Main Screen
                        onGuestContinue()
                    }
                }) {
                    Text("Continue as Guest", color = colorScheme.primary, style = Typography.bodyLarge)
                }
            }
            Card(
                modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                border = BorderStroke(1.dp, color = colorScheme.primary),
//                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
//                colors = androidx.compose.material3.CardDefaults.cardColors(Color(AppThemeColor.red, AppThemeColor.green, AppThemeColor.blue, alpha = 0.5f))
            ) {
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
                        .padding(8.dp),
                    singleLine = true,
                    leadingIcon = {Icon(imageVector = Icons.Default.Person, contentDescription = null)},
                    keyboardActions = KeyboardActions(onDone = {
                        coroutineScope.launch {
                            authViewModel.verifyCampusId(role = selectedRole, campusId = campusId)
                        }
                    }),
                    keyboardOptions = if (selectedRole == "student") KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                    else KeyboardOptions(keyboardType = KeyboardType.Ascii)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
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
                            enabled = selectedRole != role,
                            shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = if (selectedRole != role) 6.dp else 0.dp,
                                pressedElevation = 8.dp,
                                disabledElevation = 0.dp
                            ),
                            colors = ButtonDefaults.buttonColors(colorScheme.primary)
                        ) {
                            Text(role.replaceFirstChar { it.uppercase() })
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
                        .padding(8.dp),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                ) {
                    Text("Verify")
                }
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
//                    color = colorResource(R.color.verify_green)
                    color =  colorScheme.tertiary
                )
            } else if (verifyErrorMessage != null) {
                Text(
                    "$verifyErrorMessage",
                    modifier = Modifier.padding(8.dp),
                    color = colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Only show sign-up form if verified
            if (isCampusIdVerified) {
                Card(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    border = BorderStroke(1.dp, color = colorScheme.primary)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name*") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                authViewModel.onEmailChanged()
                                if (emailVerifiedFlag) {
                                    emailVerifiedFlag = false
                                }
                            },
                            label = { Text("Email*") },
                            modifier = Modifier.weight(1.7f),
                            singleLine = true,
                            leadingIcon = {Icon(imageVector = Icons.Default.Email, contentDescription = null)},
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            supportingText = { Text("Verify Your email to Proceed", color = colorScheme.error) }
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 2.dp))

                        IconButton(
                            onClick = { authViewModel.sendEmailVerification(email) },
                            enabled = isEmaiVerifyEnabled,
                            modifier = Modifier
                                .background(
                                    color = when {
                                        emailVerifiedFlag -> Color(0xFFA5D6A7)
                                        isEmaiVerifyEnabled -> Color(0xFF90B3FB)
                                        else -> Color(0xFFE0E0E0)
                                    },
                                    shape = CircleShape
                                )
                                .weight(0.3f)
                                .wrapContentSize(align = Alignment.Center)
                                .border(border = BorderStroke(2.dp,color = if(emailVerifiedFlag) Color(0xFF2E7D32) else colorScheme.primary),shape = CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verify",
                                tint = when {
                                    emailVerifiedFlag -> Color(0xFF3EC704)
                                    isEmaiVerifyEnabled -> Color(0xFF0042C6)
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
                }
                // Show status or prompt to check inbox for email verification
                emailVerificationSent?.let {
                    when (it) {
                        is Result.Success -> Text(
                            text = if(emailVerifiedFlag) "Email Verified" else "Verification email sent successfully",
                            color = colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                        is Result.Error -> Text("${it.exception.message}", color = colorScheme.error)
                        else -> {}
                    }
                }

                //Checking whether the email verified or not
                when (val result = emailVerifiedResult) {
                    is Result.Success -> {
                        if (result.data) {
                            emailVerifiedFlag = true
                            Card (
                                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                                border = BorderStroke(1.dp, color = colorScheme.primary),

                            ){
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Set New Password*") },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    supportingText = { Text("Set Your Password (min 6 characters)") },
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    trailingIcon = {
                                        val visibilityIcon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                        val description = if (passwordVisible) "Hide password" else "Show password"

                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(imageVector = visibilityIcon, contentDescription = description)
                                        }
                                    }
                                )
                                Row (
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ){
                                    Button(
                                        onClick = {
                                            authViewModel.finalizeSignUp(
                                                email,
                                                password,
                                                firstName,
                                                lastName,
                                                selectedRole,
                                                campusId
                                            )
                                        },
                                        enabled = password.length >= 6,
                                        modifier = Modifier.padding(8.dp),
                                        shape = RoundedCornerShape(
                                            topStart = 12.dp,
                                            bottomEnd = 12.dp
                                        )
                                    ) {
                                        Text("Finish Registration")
                                    }
                                }
                            }
                        } else if (pollingCompleted) {
                            // If email is not verified and polling has ended
                            Text("Email not verified yet.")
                            Button(
                                onClick = {
                                    manualCheckRequested = true
                                    authViewModel.checkEmailVerifiedManually() },
                                modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                            ) {
                                Text("Done")
                            }
                        }
                    }
                    is Result.Error -> {
                        if (pollingCompleted) {
                            Text("Check Your Inbox to Verify the Email")
                            Button(
                                onClick = {
                                    manualCheckRequested = true
                                    authViewModel.checkEmailVerifiedManually() },
                                modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                            ) {
                                Text("Done")
                            }
                        }
                    }
                    null -> {
                        if (pollingCompleted) {
                            Text("Email verification not confirmed.")
                            Button(
                                onClick = {
                                    manualCheckRequested = true
                                    authViewModel.checkEmailVerifiedManually() },
                                modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp)
                            ) {
                                Text("Done")
                            }
                        }
                    } // no result yet
                }

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
            }else{
                Text(
                    text = "Already have an account? Sign in.",
                    color = Color.Blue,
                    modifier = Modifier.padding(16.dp).clickable { onNavigateToLogin() }
                )
            }
        }
    }
}