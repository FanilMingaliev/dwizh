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
import kotlinx.coroutines.flow.Flow
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
                    description = doc.getString("description") ?: "",
                    participantCount = (doc.getLong("participantCount") ?: 0L).toInt(),
                    organizerId = doc.getString("organizerId")
                )
            } ?: emptyList()
            trySend(items)
        }
        awaitClose { registration.remove() }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * Создаёт документ в `events` с авто-id Firestore.
     * @return id нового документа (совпадает с тем, что придёт в снапшоте ленты).
     */
    suspend fun addEvent(event: Event): Result<String> {
        return runCatching {
            val organizerId = auth.currentUser?.uid
                ?: throw IllegalStateException("User is not logged in")
            val data = mapOf(
                "date" to event.date,
                "place" to event.place,
                "description" to event.description,
                "participantCount" to 0L,
                "organizerId" to organizerId
            )
            val doc = eventsCollection.document()
            doc.set(data).await()
            doc.id
        }
    }

    /** ID пользователей из `events/{eventId}/registrations`. */
    fun registrationsUserIdsFlow(eventId: String): Flow<List<String>> = callbackFlow {
        val listener = eventsCollection.document(eventId)
            .collection("registrations")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.id } ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    suspend fun registerForEvent(eventId: String): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User is not logged in"))

        return runCatching {
            val eventRef = eventsCollection.document(eventId)
            val registrationRef = eventRef.collection("registrations").document(uid)
            val data = mapOf(
                "userId" to uid,
                "registeredAt" to FieldValue.serverTimestamp()
            )
            firestore.runTransaction { tx ->
                val regSnap = tx.get(registrationRef)
                if (regSnap.exists()) {
                    return@runTransaction
                }
                tx.set(registrationRef, data)
                tx.update(eventRef, "participantCount", FieldValue.increment(1))
            }.await()
        }.map { Unit }
    }
}
