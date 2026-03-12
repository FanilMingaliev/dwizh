package com.example.authapp.ui.events

data class CreateEventUiState(
    val date: String = "",
    val place: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
