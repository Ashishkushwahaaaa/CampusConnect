package com.ashish.campusconnect.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Post(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val authorEmail: String = "",
    var thumbnailUrl: String = "",
    val attachmentUrls: List<String> = emptyList(),

    @ServerTimestamp
    val timestamp: Timestamp? = null

//    val timestamp: Long = System.currentTimeMillis(),


)