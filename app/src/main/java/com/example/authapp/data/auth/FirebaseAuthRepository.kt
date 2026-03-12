package com.example.authapp.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
            auth.signInWithEmailAndPassword(email, password).await()
        }.map { Unit }
    }

    override suspend fun register(email: String, password: String): Result<Unit> {
        return runCatching {
            auth.createUserWithEmailAndPassword(email, password).await()
        }.map { Unit }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun setCurrentUser(identifier: String) {
        if (auth.currentUser != null) return
        scope.launch {
            runCatching {
                auth.signInAnonymously().await()
            }
        }
    }
}
