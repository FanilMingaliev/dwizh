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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailAuthScreen(
    viewModel: EmailAuthViewModel,
    onNavigateBack: () -> Unit,
    onOtherMethod: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (state.isRegister) "Регистрация" else "Вход",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { viewModel.setRegisterMode(true) }) {
                Text(
                    "Регистрация",
                    color = if (state.isRegister) buttonColor else subtitleColor,
                    fontWeight = if (state.isRegister) FontWeight.SemiBold else FontWeight.Normal
                )
            }
            Text(text = " · ", color = subtitleColor)
            TextButton(onClick = { viewModel.setRegisterMode(false) }) {
                Text(
                    "Вход",
                    color = if (!state.isRegister) buttonColor else subtitleColor,
                    fontWeight = if (!state.isRegister) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (state.isRegister) {
                "Вход в приложение — по паролю, не по коду из письма. После регистрации Firebase может " +
                    "отправить письмо со ссылкой для подтверждения адреса (это не код для входа)."
            } else {
                "Введите email и пароль. Если пароль забыли — «Забыли пароль?» (на почту придёт ссылка для сброса)."
            },
            color = subtitleColor,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "E-mail", color = subtitleColor, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChange,
            placeholder = { Text("Адрес электронной почты") },
            singleLine = true,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Пароль", color = subtitleColor, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholder = { Text("Не менее 6 символов") },
            singleLine = true,
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (!state.isRegister) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { viewModel.sendPasswordReset() }, enabled = !state.isLoading) {
                Text("Забыли пароль?", color = buttonColor, fontSize = 14.sp)
            }
        }

        if (state.isRegister) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = "Пароль ещё раз", color = subtitleColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                placeholder = { Text("Повторите пароль") },
                singleLine = true,
                enabled = !state.isLoading,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        state.errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, color = Color(0xFFC62828), fontSize = 13.sp)
        }
        state.infoMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, color = Color(0xFF2E7D32), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(onClick = { viewModel.dismissInfo() }) {
                Text("Понятно", color = subtitleColor, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.submit(onRegisterSuccess, onLoginSuccess) },
            enabled = !state.isLoading && state.email.contains("@") && state.password.length >= 6 &&
                (!state.isRegister || state.confirmPassword.length >= 6),
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
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.height(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(if (state.isRegister) "Создать аккаунт" else "Войти")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onOtherMethod, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Другой способ входа", color = subtitleColor)
        }
    }
}
