package com.ashish.campusconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel: ViewModel() {
    private val userRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )
    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    private val _campusIdVerifyResult = MutableLiveData<Result<Boolean>?>()
    val campusIdVerifyResult: LiveData<Result<Boolean>> = _campusIdVerifyResult as LiveData<Result<Boolean>>

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        campusId: String
    ) {
        viewModelScope.launch {
            _authResult.value =
                userRepository.signUp(email, password, firstName, lastName, campusId)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }

    fun verifyCampusId(role: String, campusId: String) {
        viewModelScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val roleDoc = firestore.collection("campus_ids").document(role)
                val snapshot = roleDoc.get().await()
                val idList = snapshot.get("ids") as? List<*>

                if (idList != null && campusId in idList && !isCampusIdLinked(campusId)) {
                    _campusIdVerifyResult.value = Result.Success(true)
                } else {
                    throw Exception("Campus ID not found or already linked.")
                }
            } catch (e: Exception) {
                _campusIdVerifyResult.value = Result.Error(e)
            }
        }
    }

    private suspend fun isCampusIdLinked(campusId: String): Boolean {
        val firestore = FirebaseFirestore.getInstance()
        val users = firestore.collection("users")
            .whereEqualTo("campusId", campusId)
            .get().await()
        return !users.isEmpty
    }

    fun clearVerifyResult() {
        _campusIdVerifyResult.value = null
    }
}