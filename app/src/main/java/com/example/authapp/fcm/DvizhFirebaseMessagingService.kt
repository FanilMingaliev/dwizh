package com.example.authapp.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Заглушка под FCM: токен и входящие сообщения можно логировать / слать в аналитику.
 * Для пушей по чату — донастроить отправку с бэкенда и отображение уведомлений.
 */
class DvizhFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }
}
