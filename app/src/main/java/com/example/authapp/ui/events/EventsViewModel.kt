package com.example.authapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.chats.ChatsRepository
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.displayTitle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventsViewModel(
    private val repository: EventsRepository,
    private val chatsRepository: ChatsRepository
) : ViewModel() {
    val events: StateFlow<List<Event>> = repository.events

    private val _feedJumpToDate = MutableStateFlow<LocalDate?>(null)
    val feedJumpToDate: StateFlow<LocalDate?> = _feedJumpToDate.asStateFlow()

    fun requestFeedJumpToDate(date: LocalDate) {
        _feedJumpToDate.value = date
    }

    fun clearFeedJumpDate() {
        _feedJumpToDate.value = null
    }

    fun registrationsForEvent(eventId: String): Flow<List<String>> =
        repository.registrationsUserIdsFlow(eventId)

    fun registerForEvent(eventId: String, eventTitle: String) {
        viewModelScope.launch {
            val result = repository.registerForEvent(eventId)
            if (result.isSuccess) {
                chatsRepository.ensureUserInEventChat(
                    eventId,
                    eventTitle.ifBlank { "Событие" }
                )
            }
        }
    }

    fun registerForEventById(eventId: String) {
        val ev = repository.events.value.find { it.id == eventId }
        registerForEvent(eventId, ev?.displayTitle().orEmpty())
    }
}
