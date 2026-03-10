package com.example.authapp.data.model

data class UserProfile(
    val fullName: String = "",
    val interests: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val aboutMe: String = "",
    val facts: List<String> = emptyList()
)
