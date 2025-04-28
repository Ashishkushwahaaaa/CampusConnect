package com.ashish.campusconnect.viewmodel

import android.net.Uri
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
        attachmentUris: List<Uri>
    ) {
        viewModelScope.launch {
            try {
                _postState.value = PostUploadState.Loading

                var thumbnailUrl = ""
                val attachmentUrls = mutableListOf<String>()

                // Upload Thumbnail if available
                thumbnailUri?.let {
                    thumbnailUrl = repository.uploadThumbnail(it)
//                    thumbnailUrl = repository.uploadThumbnail(thumbnailUri) ?: "https://images.app.goo.gl/QLSbi2XZ4iTUXhNG9"
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
                    attachmentUrls = attachmentUrls
                )

                repository.uploadPost(post)

                _postState.value = PostUploadState.Success
            } catch (e: Exception) {
                _postState.value = PostUploadState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
