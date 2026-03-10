package com.example.authapp.ui.events

data class CreateEventUiState(
    val date: String = "",
    val place: String = "",
    val description: String = "",
    val errorMessage: String? = null
)
