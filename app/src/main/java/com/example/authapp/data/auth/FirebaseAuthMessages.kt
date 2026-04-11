package com.example.authapp.data.auth

import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.toAuthUserMessage(): String = when (this) {
    is FirebaseAuthWeakPasswordException -> "Пароль слишком слабый. Добавьте буквы и цифры."
    is FirebaseAuthUserCollisionException -> "Аккаунт с таким email уже есть. Войдите."
    is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль."
    is FirebaseTooManyRequestsException -> "Слишком много попыток. Подождите немного."
    is FirebaseAuthException -> localizedMessage?.takeIf { it.isNotBlank() }
        ?: message?.takeIf { it.isNotBlank() }
        ?: "Ошибка авторизации (${errorCode})"
    else -> localizedMessage?.takeIf { it.isNotBlank() }
        ?: message?.takeIf { it.isNotBlank() }
        ?: "Неизвестная ошибка"
}
