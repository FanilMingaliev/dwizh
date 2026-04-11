package com.example.authapp.data.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Event.userDescriptionBody(): String {
    val lines = description.lineSequence()
        .dropWhile { it.startsWith("Категория:") || it.startsWith("Тип:") }
    return lines.joinToString("\n").trim().ifBlank { description.trim() }
}

fun Event.displayCategory(): String =
    description.lineSequence()
        .firstOrNull { it.startsWith("Категория:") }
        ?.removePrefix("Категория:")
        ?.trim()
        ?.takeIf { it.isNotBlank() }
        ?: "Событие"

fun Event.displayParticipationLabel(): String? =
    description.lineSequence()
        .firstOrNull { it.startsWith("Тип:") }
        ?.removePrefix("Тип:")
        ?.trim()
        ?.takeIf { it.isNotBlank() }

fun Event.displayTitle(): String {
    val body = userDescriptionBody()
    val first = body.lineSequence().firstOrNull()?.trim().orEmpty()
    return when {
        first.isNotBlank() -> if (first.length > 90) first.take(87) + "…" else first
        description.isNotBlank() -> description.take(80).trim() + if (description.length > 80) "…" else ""
        else -> "Мероприятие"
    }
}

fun Event.placeHeadline(): String =
    place.split(",").firstOrNull()?.trim()?.ifBlank { null } ?: "Город уточняется"

fun Event.eventDate(): LocalDate? =
    runCatching { LocalDate.parse(date.trim()) }.getOrNull()

/** Строка даты для карточки ленты (коротко, по-русски). */
fun Event.displayDateLine(): String {
    val d = eventDate()
    if (d != null) {
        val fmt = DateTimeFormatter.ofPattern("EEE, d MMMM", Locale.forLanguageTag("ru"))
        return d.format(fmt).replaceFirstChar { it.uppercaseChar() }
    }
    return date.ifBlank { "Дата уточняется" }
}

/** Считается прошедшим, только если дата известна и раньше сегодня. */
fun Event.isPast(reference: LocalDate = LocalDate.now()): Boolean {
    val d = eventDate() ?: return false
    return d.isBefore(reference)
}
