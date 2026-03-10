package com.example.authapp.ui.events

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import androidx.compose.ui.platform.LocalContext
import java.time.LocalDate

private enum class DateFilter {
    Today,
    Tomorrow,
    Week,
    Custom
}

@Composable
fun EventsScreen(
    viewModel: EventsViewModel,
    onAddEvent: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current
    var filter by remember { mutableStateOf(DateFilter.Today) }
    var customDate by remember { mutableStateOf<LocalDate?>(null) }
    val scrollState = rememberScrollState()

    val filtered = filterEvents(events, filter, customDate)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF1F4F8))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = "Фильтры")
            }
            Row {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F4F8))
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F4F8))
                        .clickable { }
                        .padding(8.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = null)
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
            FilterChip(text = "Сегодня", selected = filter == DateFilter.Today) {
                filter = DateFilter.Today
            }
            FilterChip(text = "Завтра", selected = filter == DateFilter.Tomorrow) {
                filter = DateFilter.Tomorrow
            }
            FilterChip(text = "На неделе", selected = filter == DateFilter.Week) {
                filter = DateFilter.Week
            }
            FilterChip(text = "Дата", selected = filter == DateFilter.Custom) {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        customDate = LocalDate.of(year, month + 1, day)
                        filter = DateFilter.Custom
                    },
                    LocalDate.now().year,
                    LocalDate.now().monthValue - 1,
                    LocalDate.now().dayOfMonth
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Поиск") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Движ сегодня", fontWeight = FontWeight.SemiBold)
            Text(text = "${filtered.size} мероприятий", color = Color(0xFF7A8797))
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) { event ->
                EventCard(event)
            }
        }
    }
}

@Composable
private fun EventCard(event: Event) {
    val gradients = listOf(
        listOf(Color(0xFFFF9800), Color(0xFFFFC107)),
        listOf(Color(0xFF1FAE8A), Color(0xFF3ED7A3)),
        listOf(Color(0xFFFF7A5C), Color(0xFFFFB35C))
    )
    val idx = (event.id.hashCode() and 0x7fffffff) % gradients.size
    val colors = gradients[idx]

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(colors))
                .padding(16.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "Движ", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Движ",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "начало в 16:00",
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.place,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallChip("до 6")
                    SmallChip("300 ₽")
                    SmallChip("2-4 ч")
                }
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Color(0xFF1E2A3B) else Color(0xFFF1F4F8)
    val fg = if (selected) Color.White else Color(0xFF4C5B6A)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = fg)
    }
}

@Composable
private fun SmallChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 12.sp)
    }
}

private fun filterEvents(
    events: List<Event>,
    filter: DateFilter,
    customDate: LocalDate?
): List<Event> {
    val today = LocalDate.now()
    return events.filter { event ->
        val date = runCatching { LocalDate.parse(event.date) }.getOrNull()
        when (filter) {
            DateFilter.Today -> date == today
            DateFilter.Tomorrow -> date == today.plusDays(1)
            DateFilter.Week -> date != null && !date.isBefore(today) && date.isBefore(today.plusDays(7))
            DateFilter.Custom -> customDate != null && date == customDate
        }
    }.ifEmpty {
        events
    }
}
