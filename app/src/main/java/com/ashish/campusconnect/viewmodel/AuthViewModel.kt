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

class AuthViewModel: ViewModel() {
    private val userRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )
    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    fun signUp(email: String, password: String, firstName: String, lastName: String, campusId: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName, campusId)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }
}