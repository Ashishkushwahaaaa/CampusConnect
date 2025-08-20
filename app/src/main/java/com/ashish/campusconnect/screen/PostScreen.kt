package com.ashish.campusconnect.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashish.campusconnect.viewmodel.PostUploadState
import com.ashish.campusconnect.viewmodel.PostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch


@Composable
fun PostScreen(padding:PaddingValues, onPostUploaded: () -> Unit) {
    val viewModel: PostViewModel = viewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var thumbnailUri by remember { mutableStateOf<Uri?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var attachmentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "unknown@user.com"

    val thumbnailPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        thumbnailUri = uri
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

    val attachmentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        attachmentUris = uris
    }

    val postState = viewModel.postState.collectAsState()

    LaunchedEffect(postState.value) {
        if (postState.value is PostUploadState.Success) {
            onPostUploaded()
        }
    }
    // 🔑 Initialize Gemini Model (Replace with your API Key from Google AI Studio)
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyDScLyTUtmf-2KI8wKtLJq0GcyYPjNY78Q"
        )
    }


    // Scrollable content
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        val maxTitleChar = 150
        item {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    if(it.length <= maxTitleChar){
                        title = it
                    }
                },
                label = { Text("Title (${maxTitleChar-title.length} chars max) ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(64.dp, 200.dp),
                maxLines = 3
            )
        }

        // 👉 Add Auto-Generate Button
        item {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        coroutineScope.launch {
                            try {
                                val response = generativeModel.generateContent(
                                    ("Write a short professional post description for: $title")
                                )
                                description = response.text ?: "Could not generate description"
                                Log.d("GeminiResponse", response.toString())
                            } catch (e: Exception) {
                                Toast.makeText(context, "AI failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter a title first", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✨ Auto-Generate Description")
            }
        }

        item {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 200.dp), // Prevents overflowing
                maxLines = 6
            )
        }

        item {
            Button(
                onClick = { thumbnailPicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Thumbnail")
            }
        }

        item {
            // Show thumbnail preview if selected
            if (thumbnailUri != null) {
                Box(modifier = Modifier.fillMaxWidth()){
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(thumbnailUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Thumbnail Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                    IconButton(onClick = {thumbnailUri = null},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove Thumbnail")
                    }
                }

            }
        }

        item {
            Button(
                onClick = { imagePicker.launch(("image/*")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Images")
            }
        }
        item {
            if (imageUris.isNotEmpty()) {
                Text("Selected Images:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    imageUris.forEachIndexed { index, uri ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected Image ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            IconButton(
                                onClick = {
                                    imageUris = imageUris.toMutableList().also { it.removeAt(index) }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Image")
                            }
                        }
                    }
                }
            }
        }


        item {
            Button(
                onClick = { attachmentPicker.launch(arrayOf("application/pdf")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Attachments (PDFs)")
            }
        }

        // Show names of selected attachments
        items(attachmentUris.size) { index ->
            val uri = attachmentUris[index]
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = uri.lastPathSegment ?: "PDF ${index + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )

                Row {
                    IconButton(onClick = {
                        // Launch PDF Viewer Intent
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No app to view PDF", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Default.Visibility, contentDescription = "Preview PDF")

                    }
                    IconButton(
                        onClick = {
                            attachmentUris = attachmentUris.toMutableList().also { it.removeAt(index = index) }
                        }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove PDF")
                    }
                }
            }

        }

        item {
            Button(
                onClick = {
                    viewModel.uploadPost(
                        title = title,
                        description = description,
                        authorEmail = currentUserEmail,
                        thumbnailUri = thumbnailUri,
                        imageUris = imageUris,
                        attachmentUris = attachmentUris
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = postState.value !is PostUploadState.Loading
            ) {
                if (postState.value is PostUploadState.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Text("Uploading...", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("Upload Post")
                }
            }

            if (postState.value is PostUploadState.Error) {
                val message = (postState.value as PostUploadState.Error).message
                Text(
                    text = "Upload failed: $message",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview(){
    PostScreen(PaddingValues(),onPostUploaded = {})
}