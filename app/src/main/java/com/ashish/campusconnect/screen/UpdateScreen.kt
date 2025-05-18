package com.ashish.campusconnect.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.viewmodel.UpdateViewModel
import com.ashish.campusconnect.data.SessionManager
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onGuestLogin: () -> Unit,
    onUserLogout:()->Unit
) {
    val viewModel: UpdateViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = false)
    var showLogoutDialog by remember{mutableStateOf(false)}
    val upvotedPosts by viewModel.upvotedPostIds.collectAsState()

    LaunchedEffect(true) {
        viewModel.refreshPosts()
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onUserLogout()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Connect", fontWeight = FontWeight.ExtraBold)},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.top_bar_color),
                    titleContentColor = colorResource(id = R.color.black)
                ),
                actions = {
                    if (isGuest){
                        IconButton(onClick = { onGuestLogin() }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Login")
                        }
                    }else{
                        IconButton(onClick = {showLogoutDialog = true}) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Logout")
                        }
                    }
                }
            )
        },
        containerColor = colorScheme.background,
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
                .padding(10.dp)
        ) {

            items(posts) { post ->
                PostItem(
                    post = post,
                    onClick = { onPostClick(post) },
                    upvotedPosts = upvotedPosts,
                    onUpvoteClick = { viewModel.toggleUpvote(it) },
                    isGuest = isGuest
                )
            }
        }
    }
}

//format time and date
fun formatTimestamp(date: java.util.Date?): String {
    return if (date != null) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(date)
    } else {
        "Unknown"
    }
}

@Composable
fun PostItem(
    post: Post,
    onClick: () -> Unit,
    upvotedPosts: Set<String>,
    onUpvoteClick: (Post) -> Unit,
    isGuest: Boolean
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser
    val context = LocalContext.current

    var localUpvotes by remember { mutableStateOf(post.upvotes) }
    val hasUpvoted = upvotedPosts.contains(post.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.thumbnailUrl)
                    .crossfade(400)
                    .error(R.drawable.default_thumbnail)
                    .placeholder(R.drawable.baseline_arrow_down_24)
                    .build(),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            if (post.title.isNotEmpty()) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
                )
            } else {
                Text(
                    text = post.description,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = post.authorEmail, style = MaterialTheme.typography.bodySmall)
                Text(text = formatTimestamp(post.timestamp?.toDate()), style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (user == null || isGuest) {
                            Toast.makeText(context, "Login to upvote", Toast.LENGTH_SHORT).show()
                        } else {
                            localUpvotes += if (hasUpvoted) -1 else 1
                            onUpvoteClick(post)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote",
                        tint = if (hasUpvoted) Color.Green else Color.Gray
                    )
                }
                Text(
                    text = if (localUpvotes == 0) "" else localUpvotes.toString(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}