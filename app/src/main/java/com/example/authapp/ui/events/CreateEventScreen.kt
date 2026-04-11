package com.example.authapp.ui.events

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.authapp.ui.theme.DvizhColors
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel,
    onContinueToPromotion: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    val scroll = rememberScrollState()

    val picker = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selected = LocalDate.of(year, month + 1, dayOfMonth)
                viewModel.onDateChange(selected.toString())
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        )
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = DvizhColors.Brand,
        focusedLabelColor = DvizhColors.Brand,
        cursorColor = DvizhColors.Brand
    )

    Scaffold(
        containerColor = DvizhColors.ScreenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Новое событие",
                        fontWeight = FontWeight.SemiBold,
                        color = DvizhColors.Slate900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
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
            FormSectionTitle("Где")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.city,
                onValueChange = viewModel::onCityChange,
                label = { Text("Город") },
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.district,
                onValueChange = viewModel::onDistrictChange,
                label = { Text("Район / ориентир") },
                singleLine = true,
                colors = fieldColors,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))
            FormSectionTitle("Категория")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CreateEventCategoryOptions.forEach { label ->
                    val selected = state.selectedCategory == label
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onCategorySelected(label) },
                        label = { Text(label, fontSize = 14.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DvizhColors.Brand.copy(alpha = 0.14f),
                            selectedLabelColor = DvizhColors.Brand,
                            containerColor = DvizhColors.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) DvizhColors.Brand else DvizhColors.CardStroke
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            FormSectionTitle("Тип участия")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EventParticipationKind.entries.forEach { kind ->
                    val selected = state.participation == kind
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onParticipationChange(kind) },
                        label = { Text(kind.labelRu, fontSize = 14.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DvizhColors.Brand.copy(alpha = 0.14f),
                            selectedLabelColor = DvizhColors.Brand,
                            containerColor = DvizhColors.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) DvizhColors.Brand else DvizhColors.CardStroke
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            FormSectionTitle("Когда")
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.date,
                    onValueChange = viewModel::onDateChange,
                    label = { Text("Дата (ГГГГ-ММ-ДД)") },
                    singleLine = true,
                    colors = fieldColors,
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { picker.show() }) {
                    Text("Календарь", color = DvizhColors.Brand)
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            FormSectionTitle("О событии")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Описание для участников") },
                colors = fieldColors,
                shape = RoundedCornerShape(14.dp),
                minLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Отмена", color = DvizhColors.Slate600)
                }
                Button(
                    onClick = { viewModel.tryNavigateToPromotion(onContinueToPromotion) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DvizhColors.Brand),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Далее")
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
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = DvizhColors.Slate500,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
