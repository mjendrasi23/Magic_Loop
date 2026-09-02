package com.example.magicloop.ui.stash

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.magicloop.data.local.entity.YarnEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StashScreen(
    viewModel: StashViewModel,
    onBack: () -> Unit
) {
    val yarnList by viewModel.yarnList.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var yarnToEdit by remember { mutableStateOf<YarnEntity?>(null) }
    var yarnToDelete by remember { mutableStateOf<YarnEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zaliha pređe") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Natrag")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Filled.Add, contentDescription = "Dodaj pređu")
            }
        }
    ) { padding ->
        if (yarnList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Zaliha je prazna. Dodaj svoju prvu pređu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(yarnList, key = { it.id }) { yarn ->
                    YarnCard(
                        yarn = yarn,
                        onClick = { yarnToEdit = yarn },
                        onDelete = { yarnToDelete = yarn }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        YarnFormDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, brand, color, weight, grams, notes ->
                viewModel.addYarn(name, brand, color, weight, grams, notes)
                showAddDialog = false
            }
        )
    }

    yarnToEdit?.let { yarn ->
        YarnFormDialog(
            initial = yarn,
            onDismiss = { yarnToEdit = null },
            onConfirm = { name, brand, color, weight, grams, notes ->
                viewModel.updateYarn(
                    yarn.copy(
                        name = name,
                        brand = brand,
                        color = color,
                        weightCategory = weight,
                        totalGrams = grams,
                        notes = notes
                    )
                )
                yarnToEdit = null
            }
        )
    }

    yarnToDelete?.let { yarn ->
        AlertDialog(
            onDismissRequest = { yarnToDelete = null },
            title = { Text("Obriši pređu") },
            text = { Text("Jeste li sigurni da želite obrisati pređu '${yarn.name}'? Ova radnja se ne može poništiti.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteYarn(yarn)
                        yarnToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                TextButton(onClick = { yarnToDelete = null }) {
                    Text("Odustani")
                }
            }
        )
    }
}

@Composable
private fun YarnCard(
    yarn: YarnEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val remainingFraction = if (yarn.totalGrams > 0)
        (yarn.remainingGrams / yarn.totalGrams).toFloat().coerceIn(0f, 1f)
    else 0f

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = yarn.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = listOfNotNull(yarn.brand, yarn.color, yarn.weightCategory)
                            .joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "Opcije",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Obriši") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null
                                ) 
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error,
                                leadingIconColor = MaterialTheme.colorScheme.error
                            ),
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { remainingFraction },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${yarn.remainingGrams.roundedString()} g preostalo od ${yarn.totalGrams.roundedString()} g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Double.roundedString(): String =
    if (this == this.toLong().toDouble()) this.toLong().toString()
    else String.format("%.1f", this)