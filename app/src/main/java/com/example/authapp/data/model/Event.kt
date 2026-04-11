package com.example.authapp.data.model

data class Event(
    val id: String,
    val date: String,
    val place: String,
    val description: String,
    /** Счётчик из документа `events/{id}`; обновляется при регистрации. */
    val participantCount: Int = 0,
    /** Автор события (`events/{id}.organizerId`), для экрана «Мои движы». */
    val organizerId: String? = null
)
