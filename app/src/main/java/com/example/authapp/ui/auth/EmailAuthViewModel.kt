package com.example.authapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.auth.AuthRepository
import com.example.authapp.data.auth.toAuthUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmailAuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isRegister: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    /** Подсказка после сброса пароля и т.п. */
    val infoMessage: String? = null
)

class EmailAuthViewModel(
    private val authRepository: AuthRepository,
    startInRegisterMode: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailAuthUiState(isRegister = startInRegisterMode))
    val uiState: StateFlow<EmailAuthUiState> = _uiState.asStateFlow()

    fun setRegisterMode(isRegister: Boolean) {
        _uiState.update {
            it.copy(
                isRegister = isRegister,
                errorMessage = null,
                infoMessage = null,
                confirmPassword = if (!isRegister) "" else it.confirmPassword
            )
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, infoMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, infoMessage = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null, infoMessage = null) }
    }

    fun dismissInfo() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun sendPasswordReset() {
        val email = _uiState.value.email.trim()
        if (!email.contains("@") || email.length < 5) {
            _uiState.update { it.copy(errorMessage = "Введите email, на который зарегистрирован аккаунт") }
            return
        }
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            infoMessage = "Если аккаунт существует, на почту отправлено письмо со " +
                                "ссылкой для сброса пароля (не цифровой код). Проверьте папку «Спам»."
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = e.toAuthUserMessage()) }
                }
            )
        }
    }

    fun submit(onRegisterSuccess: () -> Unit, onLoginSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return

        val email = state.email.trim()
        if (!email.contains("@") || email.length < 5) {
            _uiState.update { it.copy(errorMessage = "Введите корректный email") }
            return
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Пароль не короче 6 символов") }
            return
        }
        if (state.isRegister && state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Пароли не совпадают") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = if (state.isRegister) {
                authRepository.register(email, state.password)
            } else {
                authRepository.login(email, state.password)
            }
            _uiState.update { it.copy(isLoading = false) }
            if (result.isSuccess) {
                if (state.isRegister) onRegisterSuccess() else onLoginSuccess()
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.toAuthUserMessage())
                }
            }
        }
    }
}
