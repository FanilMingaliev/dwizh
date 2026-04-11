package com.example.authapp.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.ui.theme.DvizhColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventPromotionScreen(
    viewModel: CreateEventViewModel,
    onPublished: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scroll = rememberScrollState()

    Scaffold(
        containerColor = DvizhColors.ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Продвижение",
                        fontWeight = FontWeight.SemiBold,
                        color = DvizhColors.Slate900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = DvizhColors.Slate800
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DvizhColors.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Покажите событие большей аудитории",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DvizhColors.Slate900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Выберите тариф или опубликуйте без продвижения — событие всё равно появится в ленте.",
                fontSize = 14.sp,
                color = DvizhColors.Slate600,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            PromotionTierCard(
                title = "Старт",
                subtitle = "Показ в категории и push интересующимся",
                price = "199 ₽",
                highlighted = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            PromotionTierCard(
                title = "Движ дня",
                subtitle = "Главная лента + напоминание за 2 часа",
                price = "499 ₽",
                highlighted = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            PromotionTierCard(
                title = "Максимум",
                subtitle = "Баннер + рассылка по городу",
                price = "990 ₽",
                highlighted = false
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = { viewModel.save(onPublished) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Опубликовать без продвижения", color = DvizhColors.Slate800)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.save(onPublished) },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
                shape = RoundedCornerShape(100.dp)
            ) {
                Text("Опубликовать")
            }
            if (state.isSaving) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = DvizhColors.Brand)
                }
            }
            state.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PromotionTierCard(
    title: String,
    subtitle: String,
    price: String,
    highlighted: Boolean
) {
    val borderColor = if (highlighted) DvizhColors.Brand else DvizhColors.CardStroke
    val bg = if (highlighted) DvizhColors.Brand.copy(alpha = 0.06f) else DvizhColors.White
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DvizhColors.Slate900)
            Text(price, fontWeight = FontWeight.SemiBold, color = DvizhColors.Brand, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(subtitle, fontSize = 14.sp, color = DvizhColors.Slate600, lineHeight = 20.sp)
        if (highlighted) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Рекомендуем",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = DvizhColors.Brand
            )
        }
    }
}
