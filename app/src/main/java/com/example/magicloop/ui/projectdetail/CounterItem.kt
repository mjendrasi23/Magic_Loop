package com.example.magicloop.ui.projectdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.magicloop.data.local.entity.CounterEntity

@Composable
fun CounterItem(
    counter: CounterEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit,
    onSaveSettings: (Int?, String?) -> Unit
) {
    var showEditor by remember { mutableStateOf(false) }
    var noteDraft by remember { mutableStateOf("") }
    var targetDraft by remember { mutableStateOf("") }

    LaunchedEffect(showEditor) {
        if (showEditor) {
            noteDraft = counter.note.orEmpty()
            targetDraft = counter.targetValue?.toString().orEmpty()
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = counter.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = onReset) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Resetiraj brojač")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(onClick = onDecrement) {
                    Icon(Icons.Filled.Remove, contentDescription = "Smanji")
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = counter.currentValue.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (counter.targetValue != null) {
                        Text(
                            text = "od ${counter.targetValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                FilledTonalIconButton(onClick = onIncrement) {
                    Icon(Icons.Filled.Add, contentDescription = "Povećaj")
                }
            }

            if (counter.targetValue != null && counter.targetValue > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                val progress = (counter.currentValue.toFloat() / counter.targetValue)
                    .coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (showEditor) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = targetDraft,
                        onValueChange = { if (it.all { char -> char.isDigit() }) targetDraft = it },
                        label = { Text("Ciljna vrijednost") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    OutlinedTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        label = { Text("Bilješka") },
                        placeholder = { Text("npr. 'Ponoviti red 5-8 tri puta'") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = {
                        showEditor = false
                        noteDraft = counter.note.orEmpty()
                        targetDraft = counter.targetValue?.toString().orEmpty()
                    }) { Text("Odustani") }
                    TextButton(onClick = {
                        onSaveSettings(targetDraft.toIntOrNull(), noteDraft)
                        showEditor = false
                    }) { Text("Spremi") }
                }
            } else {
                TextButton(
                    onClick = { showEditor = true },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    val label = when {
                        counter.note.isNullOrBlank() && counter.targetValue == null ->
                            "+ Dodaj bilješku ili cilj"
                        counter.note.isNullOrBlank() ->
                            "+ Dodaj bilješku  ·  Cilj: ${counter.targetValue}"
                        counter.targetValue == null ->
                            "Bilješka: ${counter.note}  ·  + Postavi cilj"
                        else ->
                            "Bilješka: ${counter.note}  ·  Cilj: ${counter.targetValue}"
                    }

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
