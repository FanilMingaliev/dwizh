package com.example.authapp.ui.profile

data class ProfileUiState(
    val fullName: String = "",
    val interests: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val aboutMe: String = "",
    val facts: List<String> = emptyList(),
    val message: String? = null
)
