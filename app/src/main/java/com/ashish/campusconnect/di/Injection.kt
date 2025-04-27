package com.ashish.campusconnect.di

import com.google.firebase.firestore.FirebaseFirestore

object Injection {
    private val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun provideFirestore(): FirebaseFirestore = instance
}