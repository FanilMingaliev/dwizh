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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

private enum class EmailAuthStep {
    EmailInput,
    CodeInput
}

@Composable
fun EmailAuthScreen(
    onNavigateBack: () -> Unit,
    onOtherMethod: () -> Unit,
    onAuthSuccess: (String) -> Unit
) {
    var step by remember { mutableStateOf(EmailAuthStep.EmailInput) }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    val isEmailValid = email.contains("@")
    val isCodeValid = code.length >= 4
    val isContinueEnabled = if (step == EmailAuthStep.EmailInput) isEmailValid else isCodeValid

    val buttonColor = Color(0xFF2F80ED)
    val disabledColor = Color(0xFFCED7E2)
    val subtitleColor = Color(0xFF73819A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = if (step == EmailAuthStep.EmailInput) "1/2" else "2/2")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "E-mail",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (step == EmailAuthStep.EmailInput) {
                "Введите e-mail, на который мы отправим ссылку\nдля авторизации"
            } else {
                "Мы отправили код на ваш адрес электронной\nпочты"
            },
            color = subtitleColor,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (step == EmailAuthStep.EmailInput) {
            Text(text = "E-mail", color = subtitleColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { value -> email = value },
                placeholder = { Text("Адрес электронной почты") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            Text(text = "Код для логина", color = subtitleColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = code,
                onValueChange = { value -> code = value },
                placeholder = { Text("Введите код") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {}) {
                Text("Отправить код повторно", color = subtitleColor)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                if (step == EmailAuthStep.EmailInput) {
                    step = EmailAuthStep.CodeInput
                } else {
                    onAuthSuccess(email)
                }
            },
            enabled = isContinueEnabled,
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
            Text("Продолжить")
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onOtherMethod, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Другой способ входа", color = subtitleColor)
        }
    }
}
