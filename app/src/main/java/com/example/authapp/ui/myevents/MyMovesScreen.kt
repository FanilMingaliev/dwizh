package com.example.authapp.ui.myevents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.isPast
import com.example.authapp.ui.events.EventListCard
import com.example.authapp.ui.theme.DvizhColors
import java.time.LocalDate

@Composable
fun MyMovesScreen(
    myEvents: List<Event>,
    onCreateMove: () -> Unit,
    onOpenEvent: (Event) -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val today = remember { LocalDate.now() }
    val active = remember(myEvents, today) {
        myEvents.filter { !it.isPast(today) }
    }
    val past = remember(myEvents, today) {
        myEvents.filter { it.isPast(today) }
    }
    val shown = if (tab == 0) active else past

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DvizhColors.ScreenBackground)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Идеи от Движа",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = DvizhColors.Slate900
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Подобрали для вас самые интересные идеи!",
            color = DvizhColors.Slate600,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCreateMove,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
            shape = RoundedCornerShape(100.dp)
        ) {
            Text("Создать движ")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Мои движы",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = DvizhColors.Slate900
        )
        Spacer(modifier = Modifier.height(8.dp))
        TabRow(
            selectedTabIndex = tab,
            containerColor = DvizhColors.White,
            contentColor = DvizhColors.Brand
        ) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Активные", fontWeight = if (tab == 0) FontWeight.SemiBold else FontWeight.Normal) }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Прошедшие", fontWeight = if (tab == 1) FontWeight.SemiBold else FontWeight.Normal) }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (myEvents.isEmpty()) {
            EmptyMyMoves(
                title = "Тут пока пусто",
                subtitle = "Создайте первый движ, чтобы он появился\nв этой вкладке",
                onCreateMove = onCreateMove
            )
        } else if (shown.isEmpty()) {
            if (tab == 1) {
                EmptyMyMoves(
                    title = "Прошедших пока нет",
                    subtitle = "Завершённые по дате события появятся здесь",
                    onCreateMove = onCreateMove,
                    showSecondaryCta = false
                )
            } else {
                EmptyMyMoves(
                    title = "Нет активных",
                    subtitle = "Все ваши события с датой уже в прошлом —\nпереключитесь на «Прошедшие»",
                    onCreateMove = onCreateMove,
                    showSecondaryCta = false
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shown, key = { it.id }) { event ->
                    EventListCard(
                        event = event,
                        onOpen = onOpenEvent,
                        onRegister = {},
                        showRegisterButton = false
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMyMoves(
    title: String,
    subtitle: String,
    onCreateMove: () -> Unit,
    showSecondaryCta: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = DvizhColors.Slate800
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = DvizhColors.Slate600,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        if (showSecondaryCta) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onCreateMove,
                colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Создать движ")
            }
        }
    }
}
