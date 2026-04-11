package com.example.authapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    val registerType: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegisterEvent, onSuccess: () -> Unit) {
        when (event) {
            is RegisterEvent.EmailChanged -> {
                val value = if (registerType == RegisterType.Phone) {
                    formatPhone(event.value)
                } else {
                    event.value
                }
                _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
            }
            is RegisterEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.value, errorMessage = null)
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPassword = event.value, errorMessage = null)
            }
            RegisterEvent.Submit -> submit(onSuccess)
        }
    }

    private fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return

        val error = validate(state)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.register(state.email, state.password)
            _uiState.value = _uiState.value.copy(isLoading = false)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    private fun validate(state: RegisterUiState): String? {
        if (registerType == RegisterType.Email) {
            if (!state.email.contains("@")) {
                return "Enter a valid email"
            }
        } else {
            return "Регистрация по телефону — с главного экрана «по номеру». Здесь только email."
        }
        if (state.password.length < 6) {
            return "Password must be at least 6 characters"
        }
        if (state.password != state.confirmPassword) {
            return "Passwords do not match"
        }
        return null
    }

    private fun formatPhone(input: String): String {
        val digits = input.filter { it.isDigit() }.take(11)
        if (digits.isEmpty()) return ""

        val hasCountry = digits.length == 11
        val country = if (hasCountry) digits.first().toString() else ""
        val rest = if (hasCountry) digits.drop(1) else digits
        val part1 = rest.take(3)
        val part2 = rest.drop(3).take(3)
        val part3 = rest.drop(6).take(2)
        val part4 = rest.drop(8).take(2)

        val builder = StringBuilder()
        if (hasCountry) {
            builder.append("+").append(country).append(" ")
        }
        if (part1.isNotEmpty()) {
            builder.append("(").append(part1).append(")")
        }
        if (part2.isNotEmpty()) {
            builder.append(" ").append(part2)
        }
        if (part3.isNotEmpty()) {
            builder.append("-").append(part3)
        }
        if (part4.isNotEmpty()) {
            builder.append("-").append(part4)
        }
        return builder.toString()
    }
}
