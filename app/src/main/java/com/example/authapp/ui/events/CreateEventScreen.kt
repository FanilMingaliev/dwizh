package com.example.authapp.ui.events

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val today = remember { LocalDate.now() }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Create event")
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.date,
            onValueChange = viewModel::onDateChange,
            label = { Text("Date (YYYY-MM-DD)") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { picker.show() }) {
                Text("Pick date")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.place,
            onValueChange = viewModel::onPlaceChange,
            label = { Text("Place") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = viewModel::onDescriptionChange,
            label = { Text("Description") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { viewModel.save(onSaveSuccess) }) {
                Text("Save")
            }
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
        state.errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it)
        }
    }
}
