// CoursesScreen.kt
package com.ashish.campusconnect.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

data class Course(
    val title: String,
    val description: String,
    val imageUrl: String,
    val duration: String,
    val instructor: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen() {
    val sampleCourses = listOf(
        Course(
            title = "Kotlin for Android",
            description = "Learn Kotlin programming for Android app development from scratch.",
            imageUrl = "https://picsum.photos/300/200?course1",
            duration = "4 weeks",
            instructor = "Ashish Sharma"
        ),
        Course(
            title = "UI/UX Design Basics",
            description = "Understand the fundamentals of user interface and user experience design.",
            imageUrl = "https://picsum.photos/300/200?course2",
            duration = "3 weeks",
            instructor = "Priti Verma"
        )
        // Add more courses here...
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Courses") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleCourses) { course ->
                CourseCard(course)
            }
        }
    }
}

@Composable
fun CourseCard(course: Course) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Image(
                painter = rememberAsyncImagePainter(course.imageUrl),
                contentDescription = course.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Instructor: ${course.instructor}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text("Duration: ${course.duration}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            Spacer(modifier = Modifier.height(6.dp))

            Text(course.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun courses() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Courses", style = MaterialTheme.typography.headlineLarge)
    }

}
