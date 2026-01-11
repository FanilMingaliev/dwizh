package com.example.authapp.data.auth

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}
