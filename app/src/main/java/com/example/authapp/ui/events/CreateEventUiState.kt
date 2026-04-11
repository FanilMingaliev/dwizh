package com.example.authapp.ui.events

enum class EventParticipationKind(val labelRu: String) {
    ALL("Все"),
    OPEN("Открытый"),
    CLOSED("Закрытый")
}

val CreateEventCategoryOptions: List<String> = listOf(
    "Спорт",
    "Игры",
    "Йога",
    "Прогулка",
    "Вечеринка",
    "Другое"
)

data class CreateEventUiState(
    val city: String = "",
    val district: String = "",
    val selectedCategory: String? = null,
    val participation: EventParticipationKind = EventParticipationKind.OPEN,
    val date: String = "",
    val description: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)
