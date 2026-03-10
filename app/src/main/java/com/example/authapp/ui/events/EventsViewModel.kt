package com.example.authapp.ui.events

import androidx.lifecycle.ViewModel
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.model.Event
import kotlinx.coroutines.flow.StateFlow

class EventsViewModel(
    repository: EventsRepository
) : ViewModel() {
    val events: StateFlow<List<Event>> = repository.events
}
