package com.example.magicloop.ui.badges

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(viewModel: BadgesViewModel, onBack: () -> Unit) {
    val badges by viewModel.badges.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Značke") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Natrag")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(badges, key = { it.definition.id.name }) { item ->
                BadgeCard(item)
            }
        }
    }
}

@Composable
private fun BadgeCard(item: BadgeUiItem) {
    val alpha = if (item.isUnlocked) 1f else 0.4f

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = iconFor(item.definition.iconName),
                contentDescription = null,
                modifier = Modifier.size(40.dp).alpha(alpha),
                tint = if (item.isUnlocked) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.definition.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (item.isUnlocked) item.definition.description else "Još nije otključano",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun iconFor(name: String): ImageVector = when (name) {
    "PlayArrow" -> Icons.Filled.PlayArrow
    "CheckCircle" -> Icons.Filled.CheckCircle
    "Stars" -> Icons.Filled.Star
    "LocalFireDepartment" -> Icons.Filled.LocalFireDepartment
    "Description" -> Icons.Filled.Description
    "Flag" -> Icons.Filled.Flag
    "PhotoCamera" -> Icons.Filled.PhotoCamera
    "Inventory" -> Icons.Filled.Inventory2
    else -> Icons.Filled.EmojiEvents
}

