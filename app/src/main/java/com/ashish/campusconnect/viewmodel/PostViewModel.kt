package com.ashish.campusconnect.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Post
import com.ashish.campusconnect.data.PostRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class PostUploadState {
    object Idle : PostUploadState()
    object Loading : PostUploadState()
    object Success : PostUploadState()
    data class Error(val message: String) : PostUploadState()
}

class PostViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _postState = MutableStateFlow<PostUploadState>(PostUploadState.Idle)
    val postState: StateFlow<PostUploadState> = _postState

    fun uploadPost(
        title: String,
        description: String,
        authorEmail: String,
        thumbnailUri: Uri?,
        imageUris: List<Uri>,
        attachmentUris: List<Uri>,
    ) {
        viewModelScope.launch {
            try {
                _postState.value = PostUploadState.Loading

                var thumbnailUrl = ""
                val imageUrls = mutableListOf<String>()
                val attachmentUrls = mutableListOf<String>()

                // Upload Thumbnail if available
                thumbnailUri?.let {
                    thumbnailUrl = repository.uploadThumbnail(it)
                }
                // Upload each image
                for (uri in imageUris) {
                    val url = repository.uploadImage(uri)
                    imageUrls.add(url)
                }

                // Upload each attachment
                for (uri in attachmentUris) {
                    val url = repository.uploadAttachment(uri)
                    attachmentUrls.add(url)
                }

                // Create Post object
                val post = Post(
                    title = title,
                    description = description,
                    authorEmail = authorEmail,
                    thumbnailUrl = thumbnailUrl,
                    imageUrl = imageUrls,
                    attachmentUrls = attachmentUrls
                )
                repository.uploadPost(post)
                _postState.value = PostUploadState.Success
            } catch (e: Exception) {
                Log.e("PostUpload", "Upload failed", e)
                _postState.value = PostUploadState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
