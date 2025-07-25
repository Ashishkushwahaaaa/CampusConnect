package com.ashish.campusconnect.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class PostRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
)
{
    suspend fun uploadThumbnail(uri: Uri): String {
        val ref = storage.reference.child("post_thumbnails/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
    suspend fun uploadImage(uri: Uri): String {
        val ref = storage.reference.child("post_images/${System.currentTimeMillis()}.jpg")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun uploadAttachment(uri: Uri): String {
        val ref = storage.reference.child("post_attachments/${System.currentTimeMillis()}.pdf")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
    suspend fun uploadPost(post: Post): Boolean {
        val postRef = firestore.collection("posts").document()
        val postWithId = post.copy(id = postRef.id)
        postRef.set(postWithId).await()
        return true
    }
}