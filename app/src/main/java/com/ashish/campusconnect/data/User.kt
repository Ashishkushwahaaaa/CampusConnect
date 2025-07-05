package com.ashish.campusconnect.data

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val campusId: String = "",
    val email: String = "",
    val role: String = "",
//    val password: String = "", //Don't save for security reason
)