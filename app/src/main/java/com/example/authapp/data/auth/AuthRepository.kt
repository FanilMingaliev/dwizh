package com.example.authapp.data.auth

import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<String?>

    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Boolean>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun logout()
}
