package com.example.authapp.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.ui.theme.DvizhColors

private data class ChatRowUi(
    val title: String,
    val subtitle: String,
    val time: String,
    val badge: String? = null
)

@Composable
fun ChatsListScreen() {
    val rows = remember {
        listOf(
            ChatRowUi("Пляжный волейбол", "Кирилл: Договорились", "15:17", "3"),
            ChatRowUi("Кирилл", "очень круто отдохнули…", "вторник", "1"),
            ChatRowUi("Прогулка", "Никита: Я опоздаю на минут 10", "Вторник", "2")
        )
    }
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DvizhColors.ScreenBackground)
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Поиск") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Все", "Группы", "Личные", "Непрочитанное").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DvizhColors.Slate200)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    color = DvizhColors.Slate800
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = row.title,
                            fontWeight = FontWeight.SemiBold,
                            color = DvizhColors.Slate900
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = row.subtitle,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            color = DvizhColors.Slate600
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = row.time,
                            fontSize = 12.sp,
                            color = DvizhColors.Slate500
                        )
                        row.badge?.let {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = it,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(DvizhColors.Brand)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                color = DvizhColors.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                HorizontalDivider(color = DvizhColors.Slate200)
            }
        }
    }
}
