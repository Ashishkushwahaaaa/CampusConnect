package com.ashish.campusconnect.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.ashish.campusconnect.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.twotone.Logout
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MotionPhotosAuto
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.twotone.Logout
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.ProfileRows

@Composable
fun ProfileScreen(
    padding: PaddingValues,
    onGuestLogin: () -> Unit,
    onLogoutRequest: () -> Unit,
    viewModel: ProfileViewModel = viewModel(),
) {
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val email = firebaseUser?.email
    val userData by viewModel.user.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }
    val rows = listOf(
        ProfileRows("Name", "${userData?.firstName} ${userData?.lastName}", Icons.Default.Face),
        ProfileRows("Email", userData?.email ?: "Guest User", Icons.Default.Mail),
        ProfileRows("Campus ID", userData?.campusId ?: "", Icons.Default.MotionPhotosAuto),
        ProfileRows("Role", userData?.role ?: "", Icons.Default.CorporateFare),
        ProfileRows("DOB", "dd/mm/yyyy", Icons.Default.CalendarMonth),
        ProfileRows("Gender","Not Define",Icons.Default.Transgender),
    )

    // Pick image launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

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
        //This Column is for Profile Box UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.padding(top = 8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(selectedImageUri ?: firebaseUser?.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(onClick = { }),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.baseline_person_24),
                    error = painterResource(id = R.drawable.baseline_person_24)
                )
                if (firebaseUser != null) {
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .background(
                                MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.8f),
                                CircleShape
                            ).wrapContentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Image",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (selectedImageUri != null && email != null) {
                Spacer(modifier = Modifier.height(12.dp))
                if (uploading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                } else {
                    Button(
                        onClick = {
                            uploading = true
                            viewModel.uploadProfileImage(
                                selectedImageUri!!,
                                email
                            ) { url, message ->
                                uploading = false
                                toastMessage = message
                                url?.let {
                                    val request = userProfileChangeRequest {
                                        photoUri = Uri.parse(it)
                                    }
                                    firebaseUser?.updateProfile(request)
                                    viewModel.loadUserProfile(email)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inversePrimary.copy(
                                alpha = 0.7f
                            )
                        ),
                    ) {
                        Text("Save")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            //Profile User Name and e-mail OR Guest User
            if (firebaseUser != null) {
                if (userData != null) {
                    Text(
                        text = "${userData!!.firstName} ${userData!!.lastName}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = userData!!.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else if (error != null) {
                    Text(text = "Error: $error", color = Color.Red)
                } else {
                    if (email != null) CircularProgressIndicator()
                }
            } else {
                Text(
                    text = "Guest User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "CampusConnect@IETAgra",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "Account Info",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        //Firstly showing Login Button and UI(based on FirebaseUser == null) and if not null then showing profile UI
        if(firebaseUser==null){
            Spacer(modifier = Modifier.height(24.dp))
            Text("Member of @IET Agra?", color = Color.Gray)
            Text("Click below to Login/Register", color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onGuestLogin,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Login", color = Color.White)
            }
        }
        else if (userData != null) {
            LazyColumn {
                items(rows) { row ->
                    ProfileDetail(type = row.type, value = row.value, icon = row.icon)
                }
                item(key = "Logout_Button",contentType = "Logout_Button") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { onLogoutRequest() }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier.fillMaxWidth().height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(32.dp)
                            )
                            Text(
                                text = "Logout",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
            }
        } else if (error != null) {
            Text(text = "Error: $error", color = Color.Red)
        } else {
            if (email != null) CircularProgressIndicator()
        }
    }
    toastMessage?.let {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        toastMessage = null
    }
}

@Composable
fun ProfileDetail(
    type: String,
    value: String,
    icon: ImageVector,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp).size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.height(60.dp)) {
            Text(
                text = type,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Visible
            )
        }
    }
}

@Preview
@Composable
 fun ProfileScreenPreview() {
    ProfileScreen(
        padding = PaddingValues(),
        onGuestLogin = TODO(),
        onLogoutRequest = TODO(),
        viewModel = TODO()
    )
 }