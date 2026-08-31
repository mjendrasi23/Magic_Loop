package com.example.magicloop.ui.projectdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.magicloop.ui.pattern.PatternViewModel
import com.example.magicloop.ui.pattern.PatternSheetSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    patternViewModel: PatternViewModel,
    onBack: () -> Unit
) {
    val project by viewModel.project.collectAsState()
    val counters by viewModel.counters.collectAsState()
    var showAddCounterDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Natrag")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCounterDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Dodaj brojač")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Brojači",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(counters, key = { it.id }) { counter ->
                CounterItem(
                    counter = counter,
                    onIncrement = { viewModel.increment(counter.id) },
                    onDecrement = { viewModel.decrement(counter.id) },
                    onReset = { viewModel.resetCounter(counter) },
                    onNoteChange = { note -> viewModel.updateNote(counter, note) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Shema",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                PatternSheetSection(viewModel = patternViewModel)
            }
        }
    }

    if (showAddCounterDialog) {
        AddCounterDialog(
            onDismiss = { showAddCounterDialog = false },
            onConfirm = { label ->
                viewModel.addCounter(label)
                showAddCounterDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCounterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var label by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novi brojač") },
        text = {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Naziv (npr. 'Redovi', 'Ponavljanja')") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(label) }, enabled = label.isNotBlank()) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}