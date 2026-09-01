package com.example.magicloop.ui.projectdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.magicloop.data.local.entity.ProjectEntity
import com.example.magicloop.data.local.entity.YarnEntity
import com.example.magicloop.ui.pattern.PatternViewModel
import com.example.magicloop.ui.pattern.PatternSheetSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    patternViewModel: PatternViewModel,
    yarnPickerViewModel: YarnPickerViewModel,
    onBack: () -> Unit,
    onShareCardClick: (Long) -> Unit
) {
    val project by viewModel.project.collectAsState()
    val counters by viewModel.counters.collectAsState()
    val images by viewModel.images.collectAsState()
    val usedYarn by yarnPickerViewModel.usedYarn.collectAsState()
    val availableYarn by yarnPickerViewModel.availableYarn.collectAsState()

    var showAddCounterDialog by remember { mutableStateOf(false) }
    var showYarnDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.secondary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Natrag")
                    }
                },actions = {
                    if (project?.status == "ACTIVE") {
                        TextButton(onClick = { showCompleteDialog = true }) {
                            Text("Završi")
                        }
                    }
                    if (project?.status == "COMPLETED") {
                        IconButton(onClick = { onShareCardClick(project!!.id) }) {
                            Icon(Icons.Filled.Share, contentDescription = "Podijeli karticu")
                        }
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
                ProjectDetailsCard(
                    project = project,
                    onEditClick = { showDetailsDialog = true }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vuna iz zalihe",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { showYarnDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Dodaj vunu iz zalihe")
                    }
                }
            }

            items(usedYarn) { item ->
                UsedYarnItem(
                    item = item,
                    onRemove = { yarnPickerViewModel.removeUsage(item.usage.id) }
                )
            }

            item {
                Text(
                    text = "Brojači",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(counters, key = { it.id }) { counter ->
                CounterItem(
                    counter = counter,
                    onIncrement = { viewModel.increment(counter.id) },
                    onDecrement = { viewModel.decrement(counter.id) },
                    onReset = { viewModel.resetCounter(counter) },
                    onSaveSettings = { target, note ->
                        viewModel.updateCounterSettings(counter, target, note)
                    }
                )
            }





            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Shema",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                PatternSheetSection(viewModel = patternViewModel)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                ProjectImagesSection(
                    images = images,
                    onAddImage = { uri -> viewModel.addImage(uri) },
                    onDeleteImage = { viewModel.deleteImage(it) }
                )
            }
        }
    }

    if (showAddCounterDialog) {
        AddCounterDialog(
            onDismiss = { showAddCounterDialog = false },
            onConfirm = { label, target ->
                viewModel.addCounter(label, target)
                showAddCounterDialog = false
            }
        )
    }

    if (showYarnDialog) {
        YarnAssignmentDialog(
            availableYarn = availableYarn,
            onDismiss = { showYarnDialog = false },
            onConfirm = { yarnId, grams ->
                yarnPickerViewModel.assignYarn(yarnId, grams)
                showYarnDialog = false
            }
        )
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = { Text("Završi projekt?") },
            text = { Text("Projekt će biti premješten u arhivu dovršenih radova.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.markCompleted()
                    showCompleteDialog = false
                }) { Text("Završi") }
            },
            dismissButton = {
                TextButton(onClick = { showCompleteDialog = false }) { Text("Odustani") }
            }
        )
    }

    if (showDetailsDialog) {
        EditProjectDetailsDialog(
            project = project,
            onDismiss = { showDetailsDialog = false },
            onConfirm = { needle, yarn, notes ->
                viewModel.updateProjectDetails(needle, yarn, notes)
                showDetailsDialog = false
            }
        )
    }
}

@Composable
private fun UsedYarnItem(
    item: YarnUsageUiItem,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.yarn?.name ?: "Nepoznata vuna",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "${item.yarn?.brand ?: ""} - ${item.yarn?.color ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${item.usage.gramsUsed} g",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Ukloni",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YarnAssignmentDialog(
    availableYarn: List<YarnEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double) -> Unit
) {
    var selectedYarn by remember { mutableStateOf<YarnEntity?>(null) }
    var gramsText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dodaj vunu iz zalihe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedYarn?.let { "${it.name} (${it.remainingGrams}g)" } ?: "Odaberi vunu",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vuna") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        availableYarn.forEach { yarn ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(yarn.name)
                                        Text(
                                            "${yarn.color} - Preostalo: ${yarn.remainingGrams}g",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                },
                                onClick = {
                                    selectedYarn = yarn
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = gramsText,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) gramsText = it },
                    label = { Text("Količina (g)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val yarnId = selectedYarn?.id
                    val grams = gramsText.toDoubleOrNull()
                    if (yarnId != null && grams != null && grams > 0) {
                        onConfirm(yarnId, grams)
                    }
                },
                enabled = selectedYarn != null && (gramsText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}

@Composable
private fun ProjectDetailsCard(
    project: ProjectEntity?,
    onEditClick: () -> Unit
) {
    if (project == null) return

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Detalji projekta", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = onEditClick) { Text("Uredi") }
            }
            Spacer(modifier = Modifier.height(4.dp))
            DetailRow("Igle", project.needleSize ?: "Nije postavljeno")
            DetailRow("Pređa", project.yarnInfo ?: "Nije postavljeno")
            if (!project.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(project.notes, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCounterDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int?) -> Unit
) {
    var label by remember { mutableStateOf("") }
    var targetValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novi brojač") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Naziv (npr. 'Redovi', 'Ponavljanja')") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { if (it.all { char -> char.isDigit() }) targetValue = it },
                    label = { Text("Cilj (opcionalno)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(label, targetValue.toIntOrNull()) },
                enabled = label.isNotBlank()
            ) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}

@Composable
private fun EditProjectDetailsDialog(
    project: ProjectEntity?,
    onDismiss: () -> Unit,
    onConfirm: (String?, String?, String?) -> Unit
) {
    var needle by remember { mutableStateOf(project?.needleSize.orEmpty()) }
    var yarn by remember { mutableStateOf(project?.yarnInfo.orEmpty()) }
    var notes by remember { mutableStateOf(project?.notes.orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Uredi detalje") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = needle, onValueChange = { needle = it },
                    label = { Text("Veličina igala") }, singleLine = true
                )
                OutlinedTextField(
                    value = yarn, onValueChange = { yarn = it },
                    label = { Text("Pređa") }, singleLine = true
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Bilješke") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    needle.ifBlank { null },
                    yarn.ifBlank { null },
                    notes.ifBlank { null }
                )
            }) { Text("Spremi") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}
