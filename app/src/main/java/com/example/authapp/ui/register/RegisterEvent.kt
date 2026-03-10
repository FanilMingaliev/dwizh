package com.example.authapp.ui.register

sealed interface RegisterEvent {
    data class EmailChanged(val value: String) : RegisterEvent
    data class PasswordChanged(val value: String) : RegisterEvent
    data class ConfirmPasswordChanged(val value: String) : RegisterEvent
    object Submit : RegisterEvent
}
