package com.example.authapp.ui.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.ChatThreadSummary
import com.example.authapp.ui.theme.DvizhColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatsListScreen(
    viewModel: ChatsViewModel,
    onOpenChat: (ChatThreadSummary) -> Unit
) {
    val rows by viewModel.chatList.collectAsState()
    var query by remember { mutableStateOf("") }
    var tab by remember { mutableIntStateOf(0) }

    val filteredByTab = remember(rows, tab) {
        when (tab) {
            0 -> rows
            1 -> rows.filter { it.kind == "event" }
            2 -> rows.filter { it.kind == "direct" }
            else -> rows
        }
    }
    val shown = remember(filteredByTab, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) filteredByTab
        else filteredByTab.filter {
            it.title.lowercase().contains(q) || it.lastPreview.lowercase().contains(q)
        }
    }

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
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DvizhColors.Brand,
                cursorColor = DvizhColors.Brand
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val labels = listOf("Все", "Группы", "Личные", "Непрочитанное")
            labels.forEachIndexed { index, label ->
                val selected = tab == index
                Text(
                    text = label,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) DvizhColors.Brand else DvizhColors.Slate200)
                        .clickable { tab = index }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    color = if (selected) DvizhColors.White else DvizhColors.Slate800,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
            items(shown, key = { it.chatId }) { row ->
                ChatRow(row, onOpenChat)
                HorizontalDivider(color = DvizhColors.Slate200)
            }
        }
    }
}

@Composable
private fun ChatRow(row: ChatThreadSummary, onOpen: (ChatThreadSummary) -> Unit) {
    val time = remember(row.updatedAtMillis) {
        if (row.updatedAtMillis <= 0L) ""
        else SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(row.updatedAtMillis))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen(row) }
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
                text = row.lastPreview.ifBlank { "Нет сообщений" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                color = DvizhColors.Slate600
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            if (time.isNotBlank()) {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = DvizhColors.Slate500
                )
            }
        }
    }
}
