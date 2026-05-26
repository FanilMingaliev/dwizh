package com.example.authapp.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    private val _currentUser = MutableStateFlow(auth.currentUser?.uid)
    override val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser?.uid
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return runCatching {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw IllegalStateException("Не удалось получить профиль пользователя")
            if (!user.isEmailVerified) {
                auth.signOut()
                throw IllegalStateException("Подтвердите email и повторите вход")
            }
        }.map { Unit }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return runCatching {
            auth.createUserWithEmailAndPassword(email, password).await()
            try {
                auth.currentUser?.sendEmailVerification()?.await()
            } catch (_: Exception) {
                // письмо подтверждения необязательно для входа; ошибка шаблона/квоты не отменяет регистрацию
            }
        }.map { Unit }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return runCatching {
            auth.sendPasswordResetEmail(email.trim()).await()
        }.map { Unit }
    }

    override suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): Result<Boolean> {
        return runCatching {
            val result = auth.signInWithCredential(credential).await()
            result.additionalUserInfo?.isNewUser == true
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
