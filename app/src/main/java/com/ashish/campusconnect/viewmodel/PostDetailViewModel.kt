package com.ashish.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDetailsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val doc = firestore.collection("posts").document(postId).get().await()
                val loadedPost = doc.toObject(Post::class.java)?.copy(id = doc.id)
                _post.value = loadedPost
            } catch (e: Exception) {
                _post.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}