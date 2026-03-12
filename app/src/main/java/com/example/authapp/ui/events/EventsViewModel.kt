package com.example.authapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.model.Event
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EventsViewModel(
    private val repository: EventsRepository
) : ViewModel() {
    val events: StateFlow<List<Event>> = repository.events

    fun registerForEvent(eventId: String) {
        viewModelScope.launch {
            repository.registerForEvent(eventId)
        }
    }
}
