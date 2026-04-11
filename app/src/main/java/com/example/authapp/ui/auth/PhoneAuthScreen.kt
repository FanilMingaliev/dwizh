package com.example.authapp.ui.auth

import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity

@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onNavigateBack: () -> Unit,
    onOtherMethod: () -> Unit,
    onSignedIn: (isNewUser: Boolean) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as? ComponentActivity

    DisposableEffect(Unit) {
        viewModel.setSignedInListener(onSignedIn)
        onDispose { viewModel.clearSignedInListener() }
    }

    val buttonColor = Color(0xFF2F80ED)
    val disabledColor = Color(0xFFCED7E2)
    val subtitleColor = Color(0xFF73819A)

    val digits = state.phone.filter { it.isDigit() }
    val isPhoneValid = digits.length >= 10
    val isCodeValid = state.code.length >= 6

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
            Text(text = if (state.step == PhoneAuthStep.PhoneInput) "1/2" else "2/2")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (state.step == PhoneAuthStep.PhoneInput) "Номер телефона" else "Код из SMS",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (state.step == PhoneAuthStep.PhoneInput) {
                "Введите номер — придёт SMS с кодом. Без кода из SMS войти нельзя."
            } else {
                "Введите 6 цифр из сообщения. В Firebase Console включите «Телефон» и добавьте SHA-256 debug-ключа для Android Studio."
            },
            color = subtitleColor,
            fontSize = 13.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (state.step == PhoneAuthStep.PhoneInput) {
            Text(text = "Номер", color = subtitleColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.phone,
                onValueChange = viewModel::onPhoneChange,
                placeholder = { Text("+7 или 8 900 123-45-67") },
                singleLine = true,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            Text(text = "Код из SMS", color = subtitleColor, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = state.code,
                onValueChange = viewModel::onCodeChange,
                placeholder = { Text("6 цифр") },
                singleLine = true,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    activity?.let { viewModel.resendVerificationCode(it) }
                },
                enabled = !state.isLoading && activity != null
            ) {
                Text("Отправить код повторно", color = subtitleColor)
            }
        }

        state.errorMessage?.let { msg ->
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = msg, color = Color(0xFFC62828), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                when (state.step) {
                    PhoneAuthStep.PhoneInput -> activity?.let { viewModel.sendVerificationCode(it) }
                    PhoneAuthStep.CodeInput -> viewModel.verifySmsCode()
                }
            },
            enabled = !state.isLoading && activity != null &&
                when (state.step) {
                    PhoneAuthStep.PhoneInput -> isPhoneValid
                    PhoneAuthStep.CodeInput -> isCodeValid
                },
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
                Text(if (state.step == PhoneAuthStep.PhoneInput) "Получить код" else "Войти")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onOtherMethod, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Другой способ входа", color = subtitleColor)
        }
    }
}
