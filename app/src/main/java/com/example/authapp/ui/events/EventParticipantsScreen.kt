package com.example.authapp.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.ui.theme.DvizhColors
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventParticipantsScreen(
    eventId: String,
    viewModel: EventsViewModel,
    onNavigateBack: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val participantCount = remember(eventId, events) {
        events.find { it.id == eventId }?.participantCount ?: 0
    }
    var registrationIds by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(eventId) {
        viewModel.registrationsForEvent(eventId).collect { registrationIds = it }
    }
    val myUid = remember { FirebaseAuth.getInstance().currentUser?.uid }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Участники") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Участники ($participantCount)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DvizhColors.Slate900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Записались через «Участвовать»",
                fontSize = 12.sp,
                color = DvizhColors.Slate500
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (registrationIds.isEmpty()) {
                Text(
                    text = "Пока никто не записался — будьте первым в карточке события.",
                    fontSize = 14.sp,
                    color = DvizhColors.Slate600,
                    lineHeight = 20.sp
                )
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(registrationIds, key = { it }) { uid ->
                        val isMe = uid == myUid
                        val label = if (isMe) "Вы" else "Участник"
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isMe) DvizhColors.Brand.copy(alpha = 0.2f)
                                        else DvizhColors.Slate200
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMe) DvizhColors.Brand else DvizhColors.Slate700
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = DvizhColors.Slate800,
                                textAlign = TextAlign.Center
                            )
                            if (!isMe) {
                                Text(
                                    text = "·${uid.takeLast(4)}",
                                    fontSize = 10.sp,
                                    color = DvizhColors.Slate400
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(DvizhColors.Slate200)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Все", color = DvizhColors.Slate800, fontWeight = FontWeight.Medium)
            }
        }
    }
}
