package com.example.authapp.ui.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.chats.ChatsRepository
import com.example.authapp.data.model.ChatMessageUi
import com.example.authapp.data.model.ChatThreadSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatsViewModel(
    private val repository: ChatsRepository
) : ViewModel() {
    val chatList: StateFlow<List<ChatThreadSummary>> = repository.chatList

    fun messagesFor(chatId: String): Flow<List<ChatMessageUi>> =
        repository.messagesFlow(chatId)

    fun send(chatId: String, text: String, onDone: (Result<Unit>) -> Unit = {}) {
        viewModelScope.launch {
            onDone(repository.sendMessage(chatId, text))
        }
    }
}
