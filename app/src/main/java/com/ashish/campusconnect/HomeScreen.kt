package com.ashish.campusconnect

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.viewmodel.HomeViewModel
import com.ashish.campusconnect.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()


    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = false)


    LaunchedEffect(true) {
        viewModel.refreshPosts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus News") }
            )
        },
        floatingActionButton =  {
            if(!isGuest){
                FloatingActionButton(onClick = onCreatePostClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Post"
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(posts) { post ->
                PostItem(post = post, onClick = { onPostClick(post) })
            }
        }
    }
}

@Composable
fun PostItem(post: Post, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.thumbnailUrl)
                    .crossfade(600)
                    .error(R.drawable.default_thumbnail) // fallback to local drawable if image fails
                    .placeholder(R.drawable.baseline_downloading_24) // optional: show while loading
                    .build(),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = post.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = post.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
