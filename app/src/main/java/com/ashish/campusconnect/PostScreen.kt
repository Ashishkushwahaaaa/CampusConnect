package com.ashish.campusconnect


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashish.campusconnect.viewmodel.PostViewModel
import com.ashish.campusconnect.viewmodel.PostUploadState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//@Composable
//fun PostScreen(
//    onPostUploaded: () -> Unit
//) {
//    val viewModel: PostViewModel = viewModel()
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }
//    var attachmentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
//
//    val thumbnailPicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        thumbnailUri = uri
//    }
//
//    val attachmentPicker = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.OpenMultipleDocuments()
//    ) { uris: List<Uri> ->
//        attachmentUris = uris
//    }
//
//    val postState = viewModel.postState.collectAsState()
//
//    LaunchedEffect(postState.value) {
//        if (postState.value is PostUploadState.Success) {
//            onPostUploaded()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        OutlinedTextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("Title") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        OutlinedTextField(
//            value = description,
//            onValueChange = { description = it },
//            label = { Text("Description") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = { thumbnailPicker.launch("image/*") },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Pick Thumbnail")
//        }
//
//        Button(
//            onClick = { attachmentPicker.launch(arrayOf("application/pdf")) },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Pick Attachments (PDFs)")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = {
//                viewModel.uploadPost(
//                    title = title,
//                    description = description,
//                    authorEmail = "demo@gmail.com", // Fetch from logged-in user dynamically
//                    thumbnailUri = thumbnailUri,
//                    attachmentUris = attachmentUris
//                )
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            if (postState.value is PostUploadState.Loading) {
//                CircularProgressIndicator(modifier = Modifier.size(24.dp))
//            } else {
//                Text("Upload Post")
//            }
//        }
//    }
//}




@Composable
fun PostScreen(onPostUploaded: () -> Unit) {
    val viewModel: PostViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }
    var attachmentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val thumbnailPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        thumbnailUri = uri
    }

    val attachmentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        attachmentUris = uris
    }

    val postState = viewModel.postState.collectAsState()

    LaunchedEffect(postState.value) {
        if (postState.value is PostUploadState.Success) {
            // Ensure the user is navigated back to the Home Screen after a successful upload
            onPostUploaded()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { thumbnailPicker.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Thumbnail")
        }

        Button(onClick = { attachmentPicker.launch(arrayOf("application/pdf")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Attachments (PDFs)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.uploadPost(
                title = title,
                description = description,
                authorEmail = "demo@gmail.com", // Replace with logged-in user's email
                thumbnailUri = thumbnailUri,
                attachmentUris = attachmentUris
            )
        }, modifier = Modifier.fillMaxWidth()) {
            if (postState.value is PostUploadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Upload Post")
            }
        }
    }
}
