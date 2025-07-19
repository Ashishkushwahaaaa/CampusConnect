package com.ashish.campusconnect.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.data.User
import com.ashish.campusconnect.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUserProfile(email: String) {
        viewModelScope.launch {
            when (val result = userRepository.getUserDetails(email)) {
                is Result.Success -> _user.value = result.data
                is Result.Error -> _error.value = result.exception.message
            }
        }
    }

    fun uploadProfileImage(
        uri: Uri,
        email: String,
        onResult: (String?, String) -> Unit
    ) {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("profileImages/${UUID.randomUUID()}.jpg")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Upload failed")
                }
                storageRef.downloadUrl
            }.addOnSuccessListener { downloadUrl ->
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(email)
                    .update("photoUrl", downloadUrl.toString())
                    .addOnSuccessListener {
                        onResult(downloadUrl.toString(), "Profile updated successfully.")
                    }
                    .addOnFailureListener {
                        onResult(null, "Photo saved but Firestore update failed.")
                    }
            }.addOnFailureListener {
                onResult(null, "Failed to upload image: ${it.message}")
            }
    }
}
