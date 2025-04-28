package com.ashish.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("posts").get().await()
                val postsList = snapshot.documents.mapNotNull {  document ->
                    val post = document.toObject(Post::class.java)
                    post?.copy(id = document.id) // Set the document ID as the post ID
                }
                _posts.value = postsList.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    fun getPostById(postId: String): Post? {
        return _posts.value.find { it.id == postId }
    }

    fun refreshPosts() {
        fetchPosts()
    }
}
