package com.example.magicloop.ui.stash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.magicloop.data.local.entity.YarnEntity

private val weightOptions = listOf(
    "Čipka", "Fingering", "Sport", "DK", "Worsted", "Aran", "Bulky", "Super Bulky"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YarnFormDialog(
    initial: YarnEntity?,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        brand: String?,
        color: String,
        weightCategory: String,
        totalGrams: Double,
        notes: String?
    ) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name.orEmpty()) }
    var brand by remember { mutableStateOf(initial?.brand.orEmpty()) }
    var color by remember { mutableStateOf(initial?.color.orEmpty()) }
    var weight by remember { mutableStateOf(initial?.weightCategory ?: weightOptions.first()) }
    var gramsText by remember { mutableStateOf(initial?.totalGrams?.toString() ?: "") }
    var notes by remember { mutableStateOf(initial?.notes.orEmpty()) }
    var weightMenuExpanded by remember { mutableStateOf(false) }

    val gramsValue = gramsText.toDoubleOrNull()
    val isValid = name.isNotBlank() && color.isNotBlank() && gramsValue != null && gramsValue > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                text = if (initial == null) "Nova pređa" else "Uredi pređu",
                color = MaterialTheme.colorScheme.primary
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Naziv") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = brand, onValueChange = { brand = it },
                    label = { Text("Proizvođač (opcionalno)") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = color, onValueChange = { color = it },
                    label = { Text("Boja") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = weightMenuExpanded,
                    onExpandedChange = { weightMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Debljina pređe") },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = weightMenuExpanded,
                        onDismissRequest = { weightMenuExpanded = false }
                    ) {
                        weightOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { weight = option; weightMenuExpanded = false }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = gramsText, onValueChange = { gramsText = it },
                    label = { Text("Ukupna količina (g)") },
                    singleLine = true,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Bilješke (opcionalno)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        name.trim(), brand.trim().ifBlank { null }, color.trim(),
                        weight, gramsValue ?: 0.0, notes.trim().ifBlank { null }
                    )
                },
                enabled = isValid
            ) { Text("Spremi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}