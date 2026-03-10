package com.example.authapp.data.auth

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<String?>(null)
    override val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(900)
        return if (email.contains("@") && password.length >= 6) {
            _currentUser.value = email
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email or password"))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        delay(900)
        return if (email.contains("@") && password.length >= 6) {
            _currentUser.value = email
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid registration data"))
        }
    }

    override fun logout() {
        _currentUser.value = null
    }

    override fun setCurrentUser(identifier: String) {
        _currentUser.value = identifier
    }
}
