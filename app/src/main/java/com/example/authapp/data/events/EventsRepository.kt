package com.example.authapp.data.events

import com.example.authapp.data.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class EventsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val eventsCollection = firestore.collection("events")

    val events: StateFlow<List<Event>> = callbackFlow {
        val registration: ListenerRegistration = eventsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val items = snapshot?.documents?.map { doc ->
                Event(
                    id = doc.id,
                    date = doc.getString("date") ?: "",
                    place = doc.getString("place") ?: "",
                    description = doc.getString("description") ?: ""
                )
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun addEvent(event: Event): Result<Unit> {
        return runCatching {
            val data = mapOf(
                "date" to event.date,
                "place" to event.place,
                "description" to event.description
            )
            eventsCollection.document().set(data).await()
        }.map { Unit }
    }

    suspend fun registerForEvent(eventId: String): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User is not logged in"))

        return runCatching {
            val registrationRef = eventsCollection.document(eventId)
                .collection("registrations")
                .document(uid)

            val data = mapOf(
                "userId" to uid,
                "registeredAt" to FieldValue.serverTimestamp()
            )
            registrationRef.set(data).await()
        }.map { Unit }
    }
}
