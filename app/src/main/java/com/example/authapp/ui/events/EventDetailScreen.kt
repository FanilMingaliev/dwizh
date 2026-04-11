package com.example.authapp.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.displayCategory
import com.example.authapp.data.model.displayParticipationLabel
import com.example.authapp.data.model.userDescriptionBody
import com.example.authapp.ui.theme.DvizhColors

private fun participantsLabel(count: Int): String = "Участники ($count)"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    viewModel: EventsViewModel,
    onNavigateBack: () -> Unit,
    onRegister: () -> Unit,
    onOpenParticipants: () -> Unit = {}
) {
    val events by viewModel.events.collectAsState()
    val event = remember(eventId, events) { events.find { it.id == eventId } }

    if (event == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Событие") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Событие не найдено", color = DvizhColors.Slate600)
            }
        }
        return
    }

    EventDetailContent(
        event = event,
        onNavigateBack = onNavigateBack,
        onRegister = onRegister,
        onOpenParticipants = onOpenParticipants
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDetailContent(
    event: Event,
    onNavigateBack: () -> Unit,
    onRegister: () -> Unit,
    onOpenParticipants: () -> Unit
) {
    val title = event.description.ifBlank { "Мероприятие" }
    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Поделиться")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DvizhColors.White,
                    titleContentColor = DvizhColors.Slate900
                )
            )
        },
        containerColor = DvizhColors.ScreenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DvizhColors.CardHeroOrangeTop, DvizhColors.CardHeroOrangeBottom)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "Спорт и активный отдых",
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Воспользуйтесь продвижением",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = event.date.ifBlank { "Дата уточняется" },
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val cat = event.displayCategory()
                        if (cat.isNotBlank() && cat != "Событие") {
                            DetailChip(cat)
                        }
                        event.displayParticipationLabel()?.let { DetailChip(it) }
                        DetailChip("до 6")
                        DetailChip("от 18 до 40")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = event.userDescriptionBody().ifBlank {
                            "Приглашаю поиграть в волейбол! Сам я любитель, поэтому просто хочу весело провести время."
                        },
                        color = Color.White.copy(alpha = 0.95f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                PromoCard(
                    title = "Покажите событие на главной уже сегодня",
                    subtitle = "Подробнее"
                )
                Spacer(modifier = Modifier.height(12.dp))
                PromoCard(
                    title = "Мгновенный охват",
                    subtitle = "Отправляем push-уведомления всем, кто интересуется этой категорией\nПодробнее"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.place.ifBlank { "Казань, адрес уточняется" },
                    color = DvizhColors.Slate700,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenParticipants)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = participantsLabel(event.participantCount),
                        color = DvizhColors.Slate900,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = DvizhColors.Slate400
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Участвовать", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("Опубликовать без продвижения", maxLines = 2, fontSize = 13.sp)
                    }
                    Button(
                        onClick = onRegister,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Text("Опубликовать", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChip(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(100.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        color = Color.White,
        fontSize = 12.sp
    )
}

@Composable
private fun PromoCard(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DvizhColors.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, color = DvizhColors.Slate900, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, color = DvizhColors.Brand, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}
