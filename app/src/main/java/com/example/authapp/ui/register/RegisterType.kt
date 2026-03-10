package com.example.authapp.ui.register

object RegisterType {
    const val Email = "email"
    const val Phone = "phone"

    fun from(value: String?): String {
        return when (value) {
            Phone -> Phone
            else -> Email
        }
    }
}
