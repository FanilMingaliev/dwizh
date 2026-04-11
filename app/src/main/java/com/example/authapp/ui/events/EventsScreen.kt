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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.ui.theme.DvizhColors
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
            .background(DvizhColors.ScreenBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { }
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
            placeholder = { Text("Поиск", color = DvizhColors.Slate500) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = DvizhColors.Slate500
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = DvizhColors.White,
                disabledTextColor = DvizhColors.Slate900,
                disabledBorderColor = DvizhColors.CardStroke,
                disabledPlaceholderColor = DvizhColors.Slate500
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Движ сегодня",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DvizhColors.Slate900
            )
            Text(
                text = "${filtered.size} мероприятий",
                color = DvizhColors.Slate500,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filtered, key = { it.id }) { event ->
                EventCardPdfStyle(event, onRegister = { selected ->
                    viewModel.registerForEvent(selected.id)
                })
            }
        }
    }
}

@Composable
private fun EventCardPdfStyle(event: Event, onRegister: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DvizhColors.CardStroke, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DvizhColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2 км",
                    color = DvizhColors.Slate500,
                    fontSize = 13.sp
                )
                Text(
                    text = "Искусство и творчество",
                    color = DvizhColors.Brand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description.ifBlank { "Мероприятие" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DvizhColors.Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.date.ifBlank { "Дата уточняется" },
                color = DvizhColors.Slate600,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("47", color = DvizhColors.Slate700, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text("Вдохновитель", color = DvizhColors.Slate600, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = event.place.ifBlank { "Организатор" },
                color = DvizhColors.Slate600,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SmallChip("до 6")
                SmallChip("2–4 ч")
                SmallChip("300 ₽")
                SmallChip("18–30")
                SmallChip("все")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(100.dp))
                    .background(DvizhColors.Brand)
                    .clickable { onRegister(event) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Участвовать",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
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

@Composable
private fun SmallChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DvizhColors.Slate100)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = DvizhColors.Slate700, fontSize = 12.sp)
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
