package com.example.magicloop.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(viewModel: ReminderSettingsViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showTimePicker by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.updateReminder(true, settings.hour, settings.minute)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Dnevni podsjetnik", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Uključi podsjetnik")
            Switch(
                checked = settings.enabled,
                onCheckedChange = { checked ->
                    if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.updateReminder(checked, settings.hour, settings.minute)
                    }
                }
            )
        }

        if (settings.enabled) {
            OutlinedButton(onClick = { showTimePicker = true }) {
                Text("Vrijeme: %02d:%02d".format(settings.hour, settings.minute))
            }
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = settings.hour,
            initialMinute = settings.minute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Odaberi vrijeme") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateReminder(
                        true, timePickerState.hour, timePickerState.minute
                    )
                    showTimePicker = false
                }) { Text("Potvrdi") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Odustani") }
            }
        )
    }
}