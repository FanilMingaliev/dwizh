package com.example.authapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent, onSuccess: () -> Unit) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.value, errorMessage = null)
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.value, errorMessage = null)
            }
            LoginEvent.Submit -> submit(onSuccess)
        }
    }

    private fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return

        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.login(state.email, state.password)
            _uiState.value = _uiState.value.copy(isLoading = false)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Ошибка входа"
                )
            }
        }
    }

    fun clearSession() {
        _uiState.value = LoginUiState()
    }
}
