package com.example.authapp.data.profile

import com.example.authapp.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private var listener: ListenerRegistration? = null

    init {
        auth.addAuthStateListener { firebaseAuth ->
            listener?.remove()
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                _profile.value = UserProfile()
                return@addAuthStateListener
            }
            listener = firestore.collection("users").document(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    val data = snapshot?.data ?: return@addSnapshotListener
                    _profile.value = UserProfile(
                        fullName = data["fullName"] as? String ?: "",
                        interests = data["interests"] as? String ?: "",
                        birthDate = data["birthDate"] as? String ?: "",
                        gender = data["gender"] as? String ?: "",
                        aboutMe = data["aboutMe"] as? String ?: "",
                        facts = (data["facts"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                    )
                }
        }
    }

    fun updateProfile(profile: UserProfile) {
        _profile.value = profile
        val uid = auth.currentUser?.uid ?: return
        scope.launch { saveProfile(uid, profile) }
    }

    suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User is not logged in"))
        return saveProfile(uid, profile)
    }

    private suspend fun saveProfile(uid: String, profile: UserProfile): Result<Unit> {
        return runCatching {
            val data = mapOf(
                "fullName" to profile.fullName,
                "interests" to profile.interests,
                "birthDate" to profile.birthDate,
                "gender" to profile.gender,
                "aboutMe" to profile.aboutMe,
                "facts" to profile.facts
            )
            firestore.collection("users").document(uid)
                .set(data, SetOptions.merge())
                .await()
        }.map { Unit }
    }
}
