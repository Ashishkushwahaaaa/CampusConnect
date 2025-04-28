package com.ashish.campusconnect

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ashish.campusconnect.data.Post

@Composable
fun PostDetailsScreen(post: Post) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            if (post.thumbnailUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(post.thumbnailUrl),
                    contentDescription = "Thumbnail",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(text = post.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = post.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (post.attachmentUrls.isNotEmpty()) {
                Text(text = "Attachments:", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                post.attachmentUrls.forEachIndexed { index, url ->
                    Text(
                        text = "Attachment ${index + 1}",
                        modifier = Modifier
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                            .padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
