package com.example.magicloop.ui.projectdetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.magicloop.data.local.entity.ProjectImageEntity

@Composable
fun ProjectImagesSection(
    images: List<ProjectImageEntity>,
    onAddImage: (Uri) -> Unit,
    onDeleteImage: (ProjectImageEntity) -> Unit
) {
    var imageToDelete by remember { mutableStateOf<ProjectImageEntity?>(null) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onAddImage(it) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fotografije",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = {
                pickMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.AddAPhoto,
                    contentDescription = "Dodaj fotografiju",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (images.isEmpty()) {
            Text(
                text = "Još nema dodanih fotografija",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(images, key = { it.id }) { image ->
                    ImageThumbnail(image = image, onDelete = { imageToDelete = image })
                }
            }
        }
    }

    if (imageToDelete != null) {
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            title = { Text("Obriši fotografiju?") },
            text = { Text("Jeste li sigurni da želite trajno obrisati ovu fotografiju?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        imageToDelete?.let { onDeleteImage(it) }
                        imageToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                TextButton(onClick = { imageToDelete = null }) {
                    Text("Odustani")
                }
            }
        )
    }
}

@Composable
private fun ImageThumbnail(
    image: ProjectImageEntity,
    onDelete: () -> Unit
) {
    Box(modifier = Modifier.size(100.dp)) {
        AsyncImage(
            model = image.imagePath,
            contentDescription = "Fotografija projekta",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
        )
        Surface(
            color = Color.Black.copy(alpha = 0.5f),
            shape = CircleShape,
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.TopEnd)
                .size(28.dp)
                .clickable(onClick = onDelete)
        ) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = "Ukloni",
                tint = Color.White,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxSize()
            )
        }
    }
}
