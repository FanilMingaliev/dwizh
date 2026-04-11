package com.example.authapp.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.auth.AuthRepository
import com.example.authapp.data.auth.toAuthUserMessage
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

enum class PhoneAuthStep {
    PhoneInput,
    CodeInput
}

data class PhoneAuthUiState(
    val step: PhoneAuthStep = PhoneAuthStep.PhoneInput,
    val phone: String = "",
    val code: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PhoneAuthViewModel(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneAuthUiState())
    val uiState: StateFlow<PhoneAuthUiState> = _uiState.asStateFlow()

    private var signedInListener: ((isNewUser: Boolean) -> Unit)? = null

    fun setSignedInListener(listener: (isNewUser: Boolean) -> Unit) {
        signedInListener = listener
    }

    fun clearSignedInListener() {
        signedInListener = null
    }

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var lastPhoneE164: String? = null

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, errorMessage = null) }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value.filter { ch -> ch.isDigit() }, errorMessage = null) }
    }

    fun sendVerificationCode(activity: Activity) {
        val e164 = normalizeRussianPhone(_uiState.value.phone) ?: run {
            _uiState.update { it.copy(errorMessage = "Введите номер в формате +7 или 10 цифр после 8/9") }
            return
        }
        if (_uiState.value.isLoading) return
        lastPhoneE164 = e164
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                completeSignInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.toAuthUserMessage()
                    )
                }
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                resendToken = token
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        step = PhoneAuthStep.CodeInput,
                        errorMessage = null
                    )
                }
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(e164)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendVerificationCode(activity: Activity) {
        val e164 = lastPhoneE164 ?: return
        val token = resendToken ?: run {
            sendVerificationCode(activity)
            return
        }
        if (_uiState.value.isLoading) return
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                completeSignInWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.toAuthUserMessage())
                }
            }

            override fun onCodeSent(id: String, newToken: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                resendToken = newToken
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(e164)
            .setTimeout(120L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifySmsCode() {
        val id = verificationId ?: run {
            _uiState.update { it.copy(errorMessage = "Сначала запросите код") }
            return
        }
        val code = _uiState.value.code
        if (code.length < 6) {
            _uiState.update { it.copy(errorMessage = "Введите код из SMS (6 цифр)") }
            return
        }
        val credential = PhoneAuthProvider.getCredential(id, code)
        completeSignInWithCredential(credential)
    }

    private fun completeSignInWithCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithPhoneCredential(credential)
            _uiState.update { it.copy(isLoading = false) }
            result.fold(
                onSuccess = { isNewUser -> signedInListener?.invoke(isNewUser) },
                onFailure = { e ->
                    _uiState.update { it.copy(errorMessage = e.toAuthUserMessage()) }
                }
            )
        }
    }
}

/** Российский номер: +7XXXXXXXXXX или 8XXXXXXXXXX или 10 цифр без кода страны */
fun normalizeRussianPhone(raw: String): String? {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.length == 11 && digits.startsWith("8") -> "+7${digits.drop(1)}"
        digits.length == 11 && digits.startsWith("7") -> "+$digits"
        digits.length == 10 -> "+7$digits"
        else -> null
    }
}
