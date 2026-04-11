package com.example.authapp.data.chats

import com.example.authapp.data.model.ChatMessageUi
import com.example.authapp.data.model.ChatThreadSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class ChatsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val chats = firestore.collection("chats")

    fun chatIdForEvent(eventId: String): String = "event_$eventId"

    val chatList: StateFlow<List<ChatThreadSummary>> = callbackFlow {
        var registration: ListenerRegistration? = null
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
        registration = firestore.collection("users").document(uid).collection("chatList")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    ChatThreadSummary(
                        chatId = doc.getString("chatId") ?: doc.id,
                        eventId = doc.getString("eventId"),
                        title = doc.getString("title") ?: "Чат",
                        lastPreview = doc.getString("lastPreview") ?: "",
                        updatedAtMillis = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L,
                        kind = doc.getString("kind") ?: "event"
                    )
                }?.sortedByDescending { it.updatedAtMillis } ?: emptyList()
                trySend(items)
            }
        awaitClose { registration?.remove() }
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun messagesFlow(chatId: String): Flow<List<ChatMessageUi>> = callbackFlow {
        val uid = auth.currentUser?.uid
        val listener = chats.document(chatId).collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(60)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.reversed()?.map { doc ->
                    ChatMessageUi(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        text = doc.getString("text") ?: "",
                        createdAtMillis = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                        mine = doc.getString("senderId") == uid
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun ensureUserInEventChat(eventId: String, title: String) {
        val uid = auth.currentUser?.uid ?: return
        val chatId = chatIdForEvent(eventId)
        val batch = firestore.batch()
        val chatRef = chats.document(chatId)
        batch.set(
            chatRef,
            mapOf(
                "kind" to "event",
                "eventId" to eventId,
                "title" to title,
                "memberIds" to FieldValue.arrayUnion(uid),
                "updatedAt" to FieldValue.serverTimestamp(),
                "lastPreview" to ""
            ),
            SetOptions.merge()
        )
        val listRef = firestore.collection("users").document(uid).collection("chatList").document(chatId)
        batch.set(
            listRef,
            mapOf(
                "chatId" to chatId,
                "eventId" to eventId,
                "title" to title,
                "kind" to "event",
                "lastPreview" to "",
                "updatedAt" to FieldValue.serverTimestamp()
            ),
            SetOptions.merge()
        )
        batch.commit().await()
    }

    suspend fun sendMessage(chatId: String, text: String): Result<Unit> {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Не авторизован"))
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return Result.success(Unit)

        return runCatching {
            val chatRef = chats.document(chatId)
            val snap = chatRef.get().await()
            val members = (snap.get("memberIds") as? List<*>)?.filterIsInstance<String>() ?: listOf(uid)
            val batch = firestore.batch()
            val msg = chatRef.collection("messages").document()
            batch.set(
                msg,
                mapOf(
                    "senderId" to uid,
                    "text" to trimmed,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            val preview = trimmed.take(120)
            val time = FieldValue.serverTimestamp()
            batch.set(
                chatRef,
                mapOf(
                    "lastPreview" to preview,
                    "updatedAt" to time
                ),
                SetOptions.merge()
            )
            for (m in members) {
                val ref = firestore.collection("users").document(m).collection("chatList").document(chatId)
                batch.set(
                    ref,
                    mapOf(
                        "chatId" to chatId,
                        "lastPreview" to preview,
                        "updatedAt" to time,
                        "title" to (snap.getString("title") ?: "Чат"),
                        "kind" to (snap.getString("kind") ?: "event"),
                        "eventId" to snap.getString("eventId")
                    ),
                    SetOptions.merge()
                )
            }
            batch.commit().await()
        }
    }
}
