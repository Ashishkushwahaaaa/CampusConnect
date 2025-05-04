package com.ashish.campusconnect.screen

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.viewmodel.HomeViewModel
import com.ashish.campusconnect.data.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPostClick: (Post) -> Unit,
    onCreatePostClick: () -> Unit,
    onGuestLogin: () -> Unit,
    onUserLogout:()->Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val posts by viewModel.posts.collectAsState()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isGuest by sessionManager.isGuest.collectAsState(initial = false)
    var showLogoutDialog by remember{mutableStateOf(false)}

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

    val upvotedPosts = remember { mutableStateListOf<String>() }

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
        containerColor = colorResource(id = R.color.background_container_color_2),
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
                PostItem(post = post, onClick = { onPostClick(post) },

                    upvotedPosts = upvotedPosts)
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

fun PostItem(post: Post, onClick: () -> Unit,upvotedPosts: SnapshotStateList<String>) {
    var localUpvotes by remember { mutableStateOf(post.upvotes) }
    val hasUpvoted = remember { mutableStateOf(upvotedPosts.contains(post.id))}

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {


            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.thumbnailUrl)
                    .crossfade(400)
                    .error(R.drawable.default_thumbnail) // fallback to local drawable if image fails
                    .placeholder(R.drawable.baseline_arrow_down_24) // optional: show while loading
                    .build(),
                contentDescription = "Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )
            //Only showing title or description(if title is not given) on the home screen
            if(post.title!=""){
                Text(text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
                )
            }else{
                Text(text = post.description,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = post.authorEmail, style = MaterialTheme.typography.bodySmall)
                //Text(text = post.timestamp?.toDate().toString(), style = MaterialTheme.typography.bodySmall)
                Text(text = formatTimestamp(post.timestamp?.toDate()), style = MaterialTheme.typography.bodySmall)

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                var hasUpvoted = false
                IconButton(onClick = {

                    if (!hasUpvoted) {
                        localUpvotes++
                        hasUpvoted= true
                        upvotedPosts.add(post.id)
                        // TODO: Trigger ViewModel to persist vote in backend
                    }
                },
                    enabled = !hasUpvoted


                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Upvote"
                    )
                }
                Text(
                    text = localUpvotes.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alignByBaseline()
                )
            }

        }
    }
}