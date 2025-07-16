package com.ashish.campusconnect.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Post(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val authorEmail: String = "",
    var thumbnailUrl: String = "",
    val imageUrl: List<String> = emptyList(),
    val attachmentUrls: List<String> = emptyList(),
    var upvotes: Int = 0,

    @ServerTimestamp
    val timestamp: Timestamp? = null,
)