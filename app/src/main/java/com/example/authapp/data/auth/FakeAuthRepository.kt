package com.example.authapp.data.auth

import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(900)
        return if (email.contains("@") && password.length >= 6) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Неверный email или пароль"))
        }
    }
}
