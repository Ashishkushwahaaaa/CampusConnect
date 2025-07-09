package com.ashish.campusconnect.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Post

@Composable
fun PostDetailsScreen(padding: PaddingValues, post: Post) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        item {
            if (post.thumbnailUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 740.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(post.thumbnailUrl)
                            .crossfade(600)
                            .error(R.drawable.default_thumbnail) // fallback to local drawable if image fails
                            .build(),
                        contentDescription = "Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(),
                        alignment = Alignment.TopCenter
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 10.dp,end = 10.dp)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 10.dp,end = 10.dp),
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (post.attachmentUrls.isNotEmpty()) {
                Text(text = "Attachments:", fontWeight = FontWeight.SemiBold,modifier = Modifier.padding(start = 10.dp,end = 10.dp))
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
