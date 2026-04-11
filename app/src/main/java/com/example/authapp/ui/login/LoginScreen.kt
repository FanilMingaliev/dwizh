package com.example.authapp.ui.login

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.R
import com.example.authapp.ui.theme.DvizhColors

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegisterEmailClick: () -> Unit,
    onRegisterPhoneClick: () -> Unit
) {
    @Suppress("UNUSED_VARIABLE")
    val unused = viewModel

    val brand = DvizhColors.Brand

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brand)
    ) {
        Image(
            painter = painterResource(R.drawable.auth_puzzle_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(280.dp)
                .offset(x = (-100).dp, y = 24.dp)
                .alpha(0.38f),
            contentScale = ContentScale.Fit
        )
        Image(
            painter = painterResource(R.drawable.auth_puzzle_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(200.dp)
                .offset(x = 40.dp, y = 120.dp)
                .alpha(0.22f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
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
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    text = "Участвуйте в интересных мероприятиях, находите новых друзей",
                    color = Color.White,
                    fontSize = 28.sp,
                    lineHeight = (28f * 1.10f).sp,
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
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRegisterEmailClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DvizhColors.White,
                        contentColor = DvizhColors.Slate900
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = DvizhColors.Slate900)
                    Text(
                        text = "по e-mail",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onRegisterPhoneClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DvizhColors.White,
                        contentColor = DvizhColors.Slate900
                    ),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = DvizhColors.Slate900)
                    Text(
                        text = "по номеру",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = DvizhColors.Slate300,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Продолжая ты соглашаешься с ")
                        }
                        withStyle(
                            SpanStyle(
                                color = DvizhColors.Slate300,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Условиями использования сервиса")
                        }
                    },
                    textAlign = TextAlign.Center
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "Проблемы со входом",
                        color = DvizhColors.Slate300,
                        fontSize = 14.sp,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
