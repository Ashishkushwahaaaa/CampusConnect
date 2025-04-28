package com.ashish.campusconnect.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.util.Log


class PostRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
)
{

//    suspend fun uploadThumbnail(uri: Uri): String {
//        val ref = storage.reference.child("post_thumbnails/${System.currentTimeMillis()}.jpg")
//        ref.putFile(uri).await()
//        return ref.downloadUrl.await().toString()
//    }
//    suspend fun uploadAttachment(uri: Uri): String {
//        val ref = storage.reference.child("post_attachments/${System.currentTimeMillis()}.pdf")
//        ref.putFile(uri).await()
//        return ref.downloadUrl.await().toString()
//    }
//    suspend fun uploadPost(post: Post): Boolean {
//        firestore.collection("posts").add(post).await()
//        return true
//    }


    suspend fun uploadThumbnail(uri: Uri): String {
        return try {
            val ref = storage.reference.child("post_thumbnails/${System.currentTimeMillis()}.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("PostRepository", "Error uploading thumbnail: ${e.message}", e)
            throw e // Re-throw to handle it in ViewModel
        }
    }

    suspend fun uploadAttachment(uri: Uri): String {
        return try {
            val ref = storage.reference.child("post_attachments/${System.currentTimeMillis()}.pdf")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("PostRepository", "Error uploading attachment: ${e.message}", e)
            throw e // Re-throw to handle it in ViewModel
        }
    }

    suspend fun uploadPost(post: Post): Boolean {
        return try {
            firestore.collection("posts").add(post).await()
            true
        } catch (e: Exception) {
            Log.e("PostRepository", "Error uploading post to Firestore: ${e.message}", e)
            throw e // Re-throw to handle it in ViewModel
        }
    }
}

