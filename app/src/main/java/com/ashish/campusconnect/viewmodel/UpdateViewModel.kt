package com.ashish.campusconnect.viewmodel

import androidx.lifecycle.ViewModel
import com.ashish.campusconnect.data.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.firestore.ListenerRegistration

class UpdateViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _upvotedPostIds = MutableStateFlow<Set<String>>(emptySet())
    val upvotedPostIds: StateFlow<Set<String>> = _upvotedPostIds

    private var postsListener: ListenerRegistration? = null

    init {
        listenToPosts()
        fetchUserUpvotedPosts()
    }

    private fun listenToPosts() {
        postsListener = firestore.collection("posts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val postsList = snapshot?.documents?.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.copy(id = doc.id)
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                _posts.value = postsList
            }
    }

    private fun fetchUserUpvotedPosts() {
        val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: return
        firestore.collection("posts")
            .whereEqualTo("upvotedBy.${userEmail.substringBefore("@")}", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val upvotedIds = snapshot?.documents?.mapNotNull { it.id }?.toSet() ?: emptySet()
                _upvotedPostIds.value = upvotedIds
            }
    }

    fun toggleUpvote(post: Post) {
        val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: return
        val postRef = firestore.collection("posts").document(post.id)
        val isCurrentlyUpvoted = _upvotedPostIds.value.contains(post.id)

        if (isCurrentlyUpvoted) {
            // Remove upvote
            postRef.update(
                mapOf(
                    "upvotes" to com.google.firebase.firestore.FieldValue.increment(-1),
                    "upvotedBy.${userEmail.substringBefore("@")}" to com.google.firebase.firestore.FieldValue.delete()
                )
            )
            _upvotedPostIds.value = _upvotedPostIds.value - post.id
        } else {
            // Add upvote
            postRef.update(
                mapOf(
                    "upvotes" to com.google.firebase.firestore.FieldValue.increment(1),
                    "upvotedBy.${userEmail.substringBefore("@")}" to true
                )
            )
            _upvotedPostIds.value = _upvotedPostIds.value + post.id
        }
    }

    override fun onCleared() {
        super.onCleared()
        postsListener?.remove()
    }

    fun refreshPosts() {
        listenToPosts()
        fetchUserUpvotedPosts()
    }
}





