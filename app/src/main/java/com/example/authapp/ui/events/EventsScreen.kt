package com.example.authapp.ui.events

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.displayCategory
import com.example.authapp.data.model.displayTitle
import com.example.authapp.data.model.eventDate
import com.example.authapp.data.model.placeHeadline
import com.example.authapp.ui.theme.DvizhColors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class DateFilter {
    Today,
    Tomorrow,
    Week,
    Custom
}

@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onAddEvent: () -> Unit,
    onOpenEvent: (Event) -> Unit,
    onOpenCalendar: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current
    var filter by remember { mutableStateOf(DateFilter.Today) }
    var customDate by remember { mutableStateOf<LocalDate?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filterCity by remember { mutableStateOf<String?>(null) }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val cityScroll = rememberScrollState()
    val categoryScroll = rememberScrollState()
    val feedJump by viewModel.feedJumpToDate.collectAsState()

    LaunchedEffect(feedJump) {
        val d = feedJump ?: return@LaunchedEffect
        customDate = d
        filter = DateFilter.Custom
        viewModel.clearFeedJumpDate()
    }

    val byDate = filterEventsByDate(events, filter, customDate)
    val byMeta = applyCityCategory(byDate, filterCity, filterCategory)
    val filtered = applySearch(byMeta, searchQuery)
    val cityOptions = remember(events) {
        events.map { it.placeHeadline() }.distinct().sorted().filter { it.isNotBlank() && it != "Город уточняется" }
    }
    val categoryOptions = remember(events) {
        events.map { it.displayCategory() }.distinct().sorted()
            .filter { it.isNotBlank() && it != "Событие" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DvizhColors.ScreenBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "ЛЕНТА",
            letterSpacing = 0.8.sp,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = DvizhColors.Slate400
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenCalendar() }
                    .background(DvizhColors.White)
                    .border(1.dp, DvizhColors.CardStroke, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = DvizhColors.Slate700,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Фильтры", color = DvizhColors.Slate800, fontWeight = FontWeight.Medium)
            }
            Row {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(DvizhColors.White)
                        .border(1.dp, DvizhColors.CardStroke, RoundedCornerShape(20.dp))
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = DvizhColors.Slate700
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DateChip(text = "Сегодня", selected = filter == DateFilter.Today) {
                filter = DateFilter.Today
            }
            DateChip(text = "Завтра", selected = filter == DateFilter.Tomorrow) {
                filter = DateFilter.Tomorrow
            }
            DateChip(text = "На неделе", selected = filter == DateFilter.Week) {
                filter = DateFilter.Week
            }
            DateChip(text = "Дата", selected = filter == DateFilter.Custom) {
                val today = LocalDate.now()
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        customDate = LocalDate.of(year, month + 1, day)
                        filter = DateFilter.Custom
                    },
                    today.year,
                    today.monthValue - 1,
                    today.dayOfMonth
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (cityOptions.isNotEmpty() || categoryOptions.isNotEmpty()) {
            Text("Город", fontSize = 12.sp, color = DvizhColors.Slate500, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(cityScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateChip(text = "Все города", selected = filterCity == null) { filterCity = null }
                cityOptions.forEach { c ->
                    DateChip(text = c, selected = filterCity == c) {
                        filterCity = if (filterCity == c) null else c
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Категория", fontSize = 12.sp, color = DvizhColors.Slate500, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(categoryScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DateChip(text = "Все", selected = filterCategory == null) { filterCategory = null }
                categoryOptions.forEach { cat ->
                    DateChip(text = cat, selected = filterCategory == cat) {
                        filterCategory = if (filterCategory == cat) null else cat
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DvizhColors.White,
            shadowElevation = 2.dp
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск по названию, месту, тексту", color = DvizhColors.Slate500) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = DvizhColors.Slate500
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DvizhColors.Brand,
                    unfocusedBorderColor = Color.Transparent,
                    focusedLabelColor = DvizhColors.Brand,
                    cursorColor = DvizhColors.Brand,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(22.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DvizhColors.Brand)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = sectionTitle(filter, customDate),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = DvizhColors.Slate900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${filtered.size} мероприятий",
                color = DvizhColors.Slate500,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (filtered.isEmpty()) {
            FeedEmptyState(
                hasAnyEvents = events.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { event ->
                    EventListCard(
                        event = event,
                        onOpen = onOpenEvent,
                        onRegister = { selected ->
                            viewModel.registerForEvent(selected.id, selected.displayTitle())
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedEmptyState(hasAnyEvents: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DvizhColors.CardHeroOrangeTop,
                            DvizhColors.Brand
                        )
                    )
                )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasAnyEvents) {
                "Ничего не нашли"
            } else {
                "Пока нет событий"
            },
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = DvizhColors.Slate800
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (hasAnyEvents) {
                "Смените фильтр даты или поисковый запрос"
            } else {
                "Создайте первый движ — кнопка «Создать» внизу"
            },
            color = DvizhColors.Slate600,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun DateChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) DvizhColors.Brand else DvizhColors.White
    val fg = if (selected) Color.White else DvizhColors.Slate700
    val border = if (selected) DvizhColors.Brand else DvizhColors.CardStroke
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .border(1.dp, border, RoundedCornerShape(100.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = fg, fontSize = 14.sp)
    }
}

private fun sectionTitle(filter: DateFilter, customDate: LocalDate?): String = when (filter) {
    DateFilter.Today -> "Движ сегодня"
    DateFilter.Tomorrow -> "Движ завтра"
    DateFilter.Week -> "На этой неделе"
    DateFilter.Custom -> {
        val d = customDate
        if (d == null) {
            "Движ по дате"
        } else {
            val fmt = DateTimeFormatter.ofPattern("d MMMM", Locale.forLanguageTag("ru"))
            "Движ · ${d.format(fmt)}"
        }
    }
}

private fun filterEventsByDate(
    events: List<Event>,
    filter: DateFilter,
    customDate: LocalDate?
): List<Event> {
    val today = LocalDate.now()
    return events.filter { event ->
        val date = event.eventDate()
        when (filter) {
            DateFilter.Today -> date == today
            DateFilter.Tomorrow -> date == today.plusDays(1)
            DateFilter.Week ->
                date != null && !date.isBefore(today) && !date.isAfter(today.plusDays(6))
            DateFilter.Custom -> customDate != null && date == customDate
        }
    }
}

private fun applyCityCategory(
    events: List<Event>,
    city: String?,
    category: String?
): List<Event> {
    var r = events
    if (city != null) {
        r = r.filter { it.placeHeadline() == city || it.place.startsWith(city, ignoreCase = true) }
    }
    if (category != null) {
        r = r.filter { it.displayCategory() == category }
    }
    return r
}

private fun applySearch(events: List<Event>, raw: String): List<Event> {
    val q = raw.trim().lowercase()
    if (q.isEmpty()) return events
    return events.filter { e ->
        e.displayTitle().lowercase().contains(q) ||
            e.place.lowercase().contains(q) ||
            e.description.lowercase().contains(q)
    }
}
