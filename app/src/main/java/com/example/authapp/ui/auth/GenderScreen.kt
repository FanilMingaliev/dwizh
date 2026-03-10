package com.example.authapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GenderScreen(
    onNavigateBack: () -> Unit,
    onContinue: (String) -> Unit
) {
    var gender by remember { mutableStateOf("Мужской") }

    val buttonColor = Color(0xFF2F80ED)
    val disabledColor = Color(0xFFCED7E2)
    val subtitleColor = Color(0xFF73819A)
    val headerColor = Color(0xFF6F7A8A)
    val selectedBg = Color(0xFF1E2A3B)
    val unselectedBg = Color(0xFFF1F4F8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Заполнение / Пол выбран",
            color = buttonColor,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "3/4", color = headerColor, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Ваш пол",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Вы можете поменять его один раз",
            color = subtitleColor,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { gender = "Мужской" },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (gender == "Мужской") selectedBg else unselectedBg,
                contentColor = if (gender == "Мужской") Color.White else subtitleColor
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Мужской")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { gender = "Женский" },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (gender == "Женский") selectedBg else unselectedBg,
                contentColor = if (gender == "Женский") Color.White else subtitleColor
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Женский")
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onContinue(gender) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = disabledColor,
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Далее")
        }
    }
}
