package com.example.authapp.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authapp.data.events.EventsRepository
import com.example.authapp.data.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class CreateEventViewModel(
    private val repository: EventsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    fun onCityChange(value: String) {
        _uiState.value = _uiState.value.copy(city = value, errorMessage = null)
    }

    fun onDistrictChange(value: String) {
        _uiState.value = _uiState.value.copy(district = value, errorMessage = null)
    }

    fun onCategorySelected(value: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = if (_uiState.value.selectedCategory == value) null else value,
            errorMessage = null
        )
    }

    fun onParticipationChange(kind: EventParticipationKind) {
        _uiState.value = _uiState.value.copy(participation = kind, errorMessage = null)
    }

    fun onDateChange(value: String) {
        _uiState.value = _uiState.value.copy(date = value, errorMessage = null)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(description = value, errorMessage = null)
    }

    /** Проверка формы перед экраном продвижения (без записи в Firestore). */
    fun tryNavigateToPromotion(onValid: () -> Unit) {
        val err = validate(_uiState.value)
        if (err != null) {
            _uiState.value = _uiState.value.copy(errorMessage = err)
            return
        }
        _uiState.value = _uiState.value.copy(errorMessage = null)
        onValid()
    }

    fun reset() {
        _uiState.value = CreateEventUiState()
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isSaving) return
        val error = validate(state)
        if (error != null) {
            _uiState.value = state.copy(errorMessage = error)
            return
        }

        val place = buildPlace(state)
        val fullDescription = buildDescription(state)

        val event = Event(
            id = "",
            date = state.date,
            place = place,
            description = fullDescription
        )
        _uiState.value = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.addEvent(event)
            if (result.isSuccess) {
                _uiState.value = CreateEventUiState()
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Не удалось сохранить событие"
                )
            }
        }
    }

    private fun buildPlace(state: CreateEventUiState): String {
        val c = state.city.trim()
        val d = state.district.trim()
        return when {
            c.isNotBlank() && d.isNotBlank() -> "$c, $d"
            c.isNotBlank() -> c
            else -> d
        }
    }

    private fun buildDescription(state: CreateEventUiState): String {
        val cat = state.selectedCategory?.let { "Категория: $it" }
        val part = "Тип: ${state.participation.labelRu}"
        val body = state.description.trim()
        return listOfNotNull(cat, part, body.takeIf { it.isNotEmpty() })
            .joinToString("\n")
            .trim()
    }

    private fun validate(state: CreateEventUiState): String? {
        if (state.city.isBlank()) {
            return "Укажите город"
        }
        if (state.district.isBlank()) {
            return "Укажите район или ориентир"
        }
        if (state.selectedCategory.isNullOrBlank()) {
            return "Выберите категорию"
        }
        if (state.date.isBlank()) {
            return "Укажите дату"
        }
        val parsed = runCatching { LocalDate.parse(state.date) }.getOrNull()
        if (parsed == null) {
            return "Формат даты: ГГГГ-ММ-ДД"
        }
        if (state.description.isBlank()) {
            return "Добавьте описание"
        }
        return null
    }
}
