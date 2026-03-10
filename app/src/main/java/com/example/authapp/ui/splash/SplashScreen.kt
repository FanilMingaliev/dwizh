package com.example.authapp.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen() {
    val background = Color(0xFF2F80ED)
    val foreground = Color.White
    val outline = Color(0xFF1F62D0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 110.dp, y = (-180).dp)
                .graphicsLayer(rotationZ = -8f)
                .border(2.dp, outline, RoundedCornerShape(22.dp))
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            PuzzleIcon(
                color = foreground,
                modifier = Modifier.graphicsLayer(rotationZ = -10f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ДВИЖ",
                color = foreground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

@Composable
private fun PuzzleIcon(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(110.dp)) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .align(Alignment.Center)
                .background(color, RoundedCornerShape(18.dp))
        )
        Box(
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.TopCenter)
                .background(color, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterEnd)
                .background(color, CircleShape)
        )
    }
}
