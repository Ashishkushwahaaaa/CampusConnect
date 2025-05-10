package com.ashish.campusconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashish.campusconnect.data.Result
import com.ashish.campusconnect.data.UserRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AuthViewModel: ViewModel() {
    private val userRepository = UserRepository(
        FirebaseAuth.getInstance(),
        FirebaseFirestore.getInstance()
    )
    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    private val _campusIdVerifyResult = MutableLiveData<Result<Boolean>?>()
    val campusIdVerifyResult: LiveData<Result<Boolean>> = _campusIdVerifyResult as LiveData<Result<Boolean>>

    private val _emailVerificationSent = MutableLiveData<Result<Boolean>?>()
    val emailVerificationSent: LiveData<Result<Boolean>> = _emailVerificationSent as LiveData<Result<Boolean>>

    private val _emailVerified = MutableLiveData<Result<Boolean>?>()
    val emailVerified: LiveData<Result<Boolean>> = _emailVerified as LiveData<Result<Boolean>>

    private val _isEmailVerifyButtonEnabled = MutableLiveData(true)
    val isEmailVerifyButtonEnabled: LiveData<Boolean> = _isEmailVerifyButtonEnabled

    private val _isPollingCompleted = MutableLiveData(false)
    val isPollingCompleted: LiveData<Boolean> = _isPollingCompleted


    fun verifyCampusId(role: String, campusId: String) {
        viewModelScope.launch {
            _campusIdVerifyResult.value = userRepository.verifyCampusId(role, campusId)
        }
    }

    fun sendEmailVerification(email: String) {
        viewModelScope.launch {
            _isEmailVerifyButtonEnabled.value = false
            _isPollingCompleted.value = false
            _emailVerificationSent.value = userRepository.createTempUserAndSendVerification(email)

            if (_emailVerificationSent.value is Result.Success) {
                // Start polling
                repeat(10) {
                    delay(5000)
                    val result = userRepository.checkEmailVerified()
                    _emailVerified.value = result
                    if (result is Result.Success && result.data) {
                        _isPollingCompleted.value = false
                        return@launch
                    }
                }
                // Timeout
                _isPollingCompleted.value = true
                _isEmailVerifyButtonEnabled.value = true
            } else {
                // Failed to send verification
                _isEmailVerifyButtonEnabled.value = true
            }
        }
    }

    fun onEmailChanged() {
        _emailVerified.value = null
        _emailVerificationSent.value = null
        _isPollingCompleted.value = false
        _isEmailVerifyButtonEnabled.value = true
    }

//    private fun generateTempPassword(): String {
//        return UUID.randomUUID().toString() + "Ab1!"
//    }

    fun checkEmailVerified() {
        viewModelScope.launch {
            _emailVerified.value =
                userRepository.checkEmailVerified()
        }
    }

    fun finalizeSignUp(
        email: String,
        realPassword: String,
        firstName: String,
        lastName: String,
        role: String,
        campusId: String
    ) {
        viewModelScope.launch {
            _authResult.value = userRepository.finalizeUserRegistration(
                email, realPassword, firstName, lastName, role, campusId
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }

    fun clearVerifyResult() {
        _campusIdVerifyResult.value = null
    }
}