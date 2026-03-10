package com.example.authapp.ui.profile

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    onAboutMe: () -> Unit,
    onFacts: () -> Unit
) {
    val titleColor = Color(0xFF1F2A3A)
    val subtitleColor = Color(0xFF73819A)
    val cardColor = Color(0xFFF6F8FB)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Профиль / редактирование",
            color = Color(0xFF2F80ED),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Редактирование", fontWeight = FontWeight.SemiBold)
            Text(text = "Просмотр", color = subtitleColor, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Мои фото", fontWeight = FontWeight.SemiBold, color = titleColor)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PhotoSlot(main = true)
            PhotoSlot()
            PhotoSlot()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PhotoSlot()
            PhotoSlot()
            PhotoSlot()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Перетащите, чтобы изменить порядок", color = subtitleColor, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Обо мне", fontWeight = FontWeight.SemiBold, color = titleColor)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileRowItem(
            title = "Расскажите о себе",
            subtitle = "Это поможет лучше узнать вас другим пользователям"
        ) { onAboutMe() }
        ProfileRowItem(
            title = "Интересные факты обо мне",
            subtitle = "Ваши новые знакомые захотят поговорить об этом!"
        ) { onFacts() }
        ProfileRowItem(
            title = "Добавьте свои интересы",
            subtitle = "Выберите чем вы увлекаетесь"
        ) { }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Дополнительно", fontWeight = FontWeight.SemiBold, color = titleColor)
        Spacer(modifier = Modifier.height(8.dp))
        ProfileRowItem(title = "Город", subtitle = "Не указано") { }
        ProfileRowItem(title = "Язык", subtitle = "Не указано") { }
        ProfileRowItem(title = "Алкоголь", subtitle = "Не указано") { }
        ProfileRowItem(title = "Курение", subtitle = "Не указано") { }
    }
}

@Composable
private fun PhotoSlot(main: Boolean = false) {
    Box(
        modifier = Modifier
            .size(84.dp)
            .background(Color(0xFFF1F4F8), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFFB8C3D1))
        if (main) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = "Главное фото", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun ProfileRowItem(title: String, subtitle: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF6F8FB), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = Color(0xFF8A97A7), fontSize = 12.sp)
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF8A97A7)
            )
        }
    }
}
