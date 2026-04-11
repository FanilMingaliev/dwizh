package com.example.authapp.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.eventDate
import com.example.authapp.ui.theme.DvizhColors
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsCalendarScreen(
    events: List<Event>,
    onPickDate: (LocalDate) -> Unit,
    onClose: () -> Unit
) {
    var ym by remember { mutableStateOf(YearMonth.now()) }
    val counts = remember(events, ym) {
        val map = mutableMapOf<LocalDate, Int>()
        for (e in events) {
            val d = e.eventDate() ?: continue
            if (YearMonth.from(d) == ym) {
                map[d] = (map[d] ?: 0) + 1
            }
        }
        map
    }

    Scaffold(
        containerColor = DvizhColors.ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${ym.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru"))} ${ym.year}",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    TextButton(onClick = { ym = ym.minusMonths(1) }) { Text("‹") }
                    TextButton(onClick = { ym = ym.plusMonths(1) }) { Text("›") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DvizhColors.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { d ->
                    Text(
                        d,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = DvizhColors.Slate500,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val first = ym.atDay(1)
            val offset = (first.dayOfWeek.value + 6) % 7
            val daysInMonth = ym.lengthOfMonth()
            val cells = (0 until offset).map { null } + (1..daysInMonth).map { ym.atDay(it) }
            val rows = cells.chunked(7)
            for (row in rows) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (cell in row) {
                        if (cell == null) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            val n = counts[cell] ?: 0
                            CalendarDayCell(
                                date = cell,
                                count = n,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (n > 0) onPickDate(cell)
                                }
                            )
                        }
                    }
                    if (row.size < 7) {
                        repeat(7 - row.size) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Нажмите на день с числом — откроется лента с фильтром по этой дате.",
                style = MaterialTheme.typography.bodySmall,
                color = DvizhColors.Slate600,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isToday) 2.dp else 1.dp,
                color = if (isToday) DvizhColors.Brand else DvizhColors.CardStroke,
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (count > 0) DvizhColors.Brand.copy(alpha = 0.08f) else DvizhColors.White)
            .clickable(enabled = count > 0, onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${date.dayOfMonth}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = DvizhColors.Slate900
            )
            if (count > 0) {
                Text(
                    text = "$count соб.",
                    fontSize = 10.sp,
                    color = DvizhColors.Brand,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
