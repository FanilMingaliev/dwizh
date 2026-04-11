package com.example.authapp.ui.myevents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.authapp.ui.theme.DvizhColors

@Composable
fun MyMovesScreen(
    onCreateMove: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
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
        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Активные") }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Прошедшие") }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Тут пока пусто",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = DvizhColors.Slate800
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Создайте первый движ, чтобы начать\nисторию",
                color = DvizhColors.Slate600,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
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
