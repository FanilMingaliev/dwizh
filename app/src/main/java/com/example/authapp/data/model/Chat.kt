package com.example.authapp.data.model

data class ChatThreadSummary(
    val chatId: String,
    val eventId: String?,
    val title: String,
    val lastPreview: String,
    val updatedAtMillis: Long,
    val kind: String
)

data class ChatMessageUi(
    val id: String,
    val senderId: String,
    val text: String,
    val createdAtMillis: Long,
    val mine: Boolean
)
