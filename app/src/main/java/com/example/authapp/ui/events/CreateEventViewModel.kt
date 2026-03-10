package com.example.authapp.ui.events

import androidx.lifecycle.ViewModel
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class CreateEventViewModel(
    private val repository: EventsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun onDateChange(value: String) {
        _uiState.value = _uiState.value.copy(date = value, errorMessage = null)
    }

    fun onPlaceChange(value: String) {
        _uiState.value = _uiState.value.copy(place = value, errorMessage = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = null)
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val error = validate(state)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        val event = Event(
            id = System.currentTimeMillis().toString(),
            date = state.date,
            place = state.place,
            description = state.description
        )
        repository.addEvent(event)
        _uiState.value = CreateEventUiState()
        onSuccess()
    }

    private fun validate(state: CreateEventUiState): String? {
        if (state.date.isBlank()) {
            return "Date is required"
        }
        val parsed = runCatching { LocalDate.parse(state.date) }.getOrNull()
        if (parsed == null) {
            return "Use date format YYYY-MM-DD"
        }
        if (state.place.isBlank()) {
            return "Place is required"
        }
        if (state.description.isBlank()) {
            return "Description is required"
        }
        return null
    }
}
