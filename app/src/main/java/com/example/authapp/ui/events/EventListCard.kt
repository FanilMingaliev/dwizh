package com.example.authapp.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.data.model.Event
import com.example.authapp.data.model.displayCategory
import com.example.authapp.data.model.displayDateLine
import com.example.authapp.data.model.displayParticipationLabel
import com.example.authapp.data.model.displayTitle
import com.example.authapp.data.model.placeHeadline
import com.example.authapp.ui.theme.DvizhColors

private val CardShape = RoundedCornerShape(20.dp)

@Composable
fun EventListCard(
    event: Event,
    onOpen: (Event) -> Unit,
    onRegister: (Event) -> Unit,
    showRegisterButton: Boolean = true
) {
    val typeLabel = event.displayParticipationLabel()
    val category = event.displayCategory()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = CardShape,
        colors = CardDefaults.cardColors(containerColor = DvizhColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.clip(CardShape)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(event) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DvizhColors.CardHeroOrangeTop,
                                    DvizhColors.CardHeroOrangeBottom
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = category,
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = event.displayTitle(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = event.displayDateLine(),
                            color = Color.White.copy(alpha = 0.95f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DvizhColors.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Рядом · ${event.placeHeadline()}",
                            color = DvizhColors.Slate500,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Вдохновитель",
                            color = DvizhColors.Slate400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${event.participantCount}",
                            color = DvizhColors.Slate800,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "идут",
                            color = DvizhColors.Slate600,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "·",
                            color = DvizhColors.Slate300,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "2–4 ч",
                            color = DvizhColors.Slate500,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.place.ifBlank { "Место уточняется" },
                        color = DvizhColors.Slate600,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        typeLabel?.let { FeedMetaChip(it, accent = true) }
                        FeedMetaChip(
                            when (event.participantCount) {
                                0 -> "Будьте первым"
                                1 -> "1 человек"
                                in 2..4 -> "${event.participantCount} человека"
                                else -> "${event.participantCount} человек"
                            },
                            accent = false
                        )
                        FeedMetaChip("18+", accent = false)
                    }
                }
            }

            if (showRegisterButton) {
                HorizontalDivider(color = DvizhColors.Slate100, thickness = 1.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DvizhColors.White)
                        .clickable { onRegister(event) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Участвовать",
                        color = DvizhColors.Brand,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedMetaChip(text: String, accent: Boolean) {
    val bg = if (accent) DvizhColors.Brand.copy(alpha = 0.12f) else DvizhColors.Slate100
    val fg = if (accent) DvizhColors.Brand else DvizhColors.Slate700
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 12.sp,
            fontWeight = if (accent) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
