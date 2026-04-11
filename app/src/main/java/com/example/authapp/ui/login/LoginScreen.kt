package com.example.authapp.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterEmailClick: () -> Unit,
    onRegisterPhoneClick: () -> Unit
) {
    // Keep signature for compatibility with current nav graph.
    // This landing screen intentionally does not require view-model state.
    @Suppress("UNUSED_VARIABLE")
    val unused = viewModel

    val background = Color(0xFF2F80ED)
    val lightPuzzle = Color(0xFFFFFFFF).copy(alpha = 0.10f)
    val darkPuzzle = Color(0xFF1C60CD).copy(alpha = 0.38f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-40).dp, y = 120.dp)
                .background(darkPuzzle, RoundedCornerShape(32.dp))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 240.dp, y = 160.dp)
                .background(lightPuzzle, RoundedCornerShape(24.dp))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 260.dp, y = 340.dp)
                .background(lightPuzzle, RoundedCornerShape(18.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    )
                    Text(
                        text = "ДВИЖ",
                        color = Color.White,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(220.dp))
                Text(
                    text = "Участвуйте в интересных\nмероприятиях, находите\nновых друзей",
                    color = Color.White,
                    fontSize = 43.sp,
                    lineHeight = 46.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Авторизация",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onRegisterEmailClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                    Text(text = "по e-mail", fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onRegisterPhoneClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                    Text(text = "по номеру", fontSize = 22.sp, modifier = Modifier.padding(start = 8.dp))
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Продолжая ты соглашаешься с Условиями\nиспользования сервиса",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "Проблемы со входом",
                        color = Color.White,
                        modifier = Modifier.alpha(0.9f)
                    )
                }
            }
        }
    }
}




