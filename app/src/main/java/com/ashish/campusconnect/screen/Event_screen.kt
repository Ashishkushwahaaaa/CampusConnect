// EventScreen.kt
package com.ashish.campusconnect.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

data class Event(
    val title: String,
    val description: String,
    val imageUrl: String,
    val date: String,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Event_screen() {
    val sampleEvents = listOf(
        Event(
            title = "Tech Fest 2025",
            description = "Explore the world of technology with workshops, coding competitions and more.",
            imageUrl = "https://picsum.photos/300/200?1",
            date = "May 25, 2025",
            location = "Auditorium Hall"
        ),
        Event(
            title = "Startup Meet",
            description = "Meet and connect with aspiring entrepreneurs and VCs.",
            imageUrl = "https://picsum.photos/300/200?2",
            date = "June 10, 2025",
            location = "Seminar Room B"
        )
        // Add more events as needed
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upcoming Events") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleEvents) { event ->
                EventCard(event)
            }
        }
    }
}

@Composable
fun EventCard(event: Event) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(event.imageUrl),
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = event.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = event.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
