package com.ashish.campusconnect.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ashish.campusconnect.R
import com.ashish.campusconnect.data.Contact
import com.ashish.campusconnect.data.Post

@Composable
fun PostDetailsScreen(padding: PaddingValues, post: Post) {
    val context = LocalContext.current
    val contactUs = listOf (
        Contact("Call Us", "Call", Icons.Default.Call),
        Contact("Mail Us", "Mail", Icons.Default.Mail),
        Contact("Whatsapp Us" ,"Whatsapp", Icons.Default.Whatsapp)
    )
    val footerTag = listOf<String>("Developers", "Subscription", "Promotions", "Products", "Services", "Features", "Security", "Report", "Setting", "Account")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        //ImageSlider/thumbnail
        item {
            if (post.imageUrl.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .heightIn(350.dp, 720.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    NetworkImageSlider(images = post.imageUrl)
                }
            } else {
                // If there is no ImageContent in the PostDetail, then the thumbnail will be shown
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16f / 9f)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(post.thumbnailUrl)
                            .crossfade(true)
                            .error(R.drawable.default_thumbnail)
                            .build(),
                        contentDescription = "ThumbnailContent",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        //Title and divider
        item {
            Text(
                text = if (post.title.isEmpty()) "CampusConnect@IETAgra" else post.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        //Description
        item {
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp,bottom = 16.dp),
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
        //Attachments
        item {
            if (post.attachmentUrls.isNotEmpty()) {
                Text(
                    text = "Attachments:",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp,end = 10.dp)
                )
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
            Spacer(modifier = Modifier.height(16.dp))
        }
        //Footer
        item {
            Spacer(modifier = Modifier.height(24.dp))
            FooterSection(post, contactUs, footerTag)
        }
    }
}

@Composable
fun FooterSection(post: Post, contactUs: List<Contact>, footerTag: List<String>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = "Posted By : ${post.authorEmail.substringBefore("@")}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Contact Us",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Row(modifier = Modifier.padding(bottom = 12.dp)) {
                contactUs.forEach { contact ->
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = contact.icon,
                            contentDescription = contact.desc,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Text(
                text = "CampusConnect@IETAgra",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(110.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
                    .padding(vertical = 8.dp)
            ) {
                items(footerTag) { item ->
                    TextButton(
                        onClick = {
                            // Handle click actions
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}