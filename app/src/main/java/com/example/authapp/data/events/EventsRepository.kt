package com.example.authapp.data.events

import com.example.authapp.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EventsRepository {
    private val items = mutableListOf<Event>()
    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    fun addEvent(event: Event) {
        items.add(event)
        _events.value = items.toList()
    }
}
