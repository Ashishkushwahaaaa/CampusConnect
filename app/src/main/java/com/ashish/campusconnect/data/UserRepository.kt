package com.ashish.campusconnect.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signUp(email: String, password: String, firstName: String, lastName: String, campusId: String): Result<Boolean> {
        return try{
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(firstName, lastName, campusId, email)
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: Exception){
            Result.Error(e)
        }
    }

    private suspend fun saveUserToFirestore(user: User){
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}