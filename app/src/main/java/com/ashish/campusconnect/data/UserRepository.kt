package com.ashish.campusconnect.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // Temporarily store FirebaseUser for reuse
    private var tempFirebaseUser: FirebaseUser? = null

    suspend fun verifyCampusId(role: String, campusId: String): Result<Boolean> {
        return try {
            val doc = firestore.collection("campus_ids").document(role).get().await()
            val idMap = doc.get("ids") as? Map<*, *>
                ?: throw Exception("No campus IDs exists for Selected role.")

            val status = idMap[campusId] as? Boolean
                ?: throw Exception("Campus ID not found.")

            if (!status) Result.Success(true) //status==false (id is valid and not linked)
            else throw Exception("Campus ID already linked to another account.")

        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun createTempUserAndSendVerification(email: String): Result<Boolean> {
        return try {
            auth.currentUser?.delete() // Delete existing temp user
            val result = auth.createUserWithEmailAndPassword(email, "Temp@1234").await()
            val user = result.user ?: throw Exception("User creation failed")

            tempFirebaseUser = user
            user.sendEmailVerification().await()

            firestore.collection("incompleteSignups").document(user.uid)
                .set(mapOf("email" to email, "createdAt" to FieldValue.serverTimestamp())).await()

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun checkEmailVerified(): Result<Boolean> {
        return try {
            val user = tempFirebaseUser ?: auth.currentUser
            user?.reload()?.await()
            if (user?.isEmailVerified == true) {
                Result.Success(true)
            } else {
                throw Exception("Email not yet verified.")
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun finalizeUserRegistration(
        email: String,
        realPassword: String,
        firstName: String,
        lastName: String,
        role: String,
        campusId: String
    ): Result<Boolean> {
        return try {
            val user = tempFirebaseUser ?: auth.currentUser
            user ?: throw Exception("No user found.")

            user.updatePassword(realPassword).await()

            val userData = hashMapOf(
                "email" to email,
                "firstName" to firstName,
                "lastName" to lastName,
                "role" to role,
                "campusId" to campusId,
                "timestamp" to FieldValue.serverTimestamp(),
//                "password" to realPassword //Never save this data for security reason
            )

            firestore.collection("users").document(email).set(userData).await()

            val campusIdDoc = firestore.collection("campus_ids").document(role)
            val fieldPath = FieldPath.of("ids", campusId)
            campusIdDoc.update(fieldPath, true).await()

            // 🔹 Delete incomplete signup flag
            firestore.collection("incompleteSignups").document(user.uid).delete().await()

            Result.Success(true)
        } catch (e: Exception) {
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

    suspend fun getUserDetails(email: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(email).get().await()
            val user = doc.toObject(User::class.java)
                ?: throw Exception("User data not found")
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}