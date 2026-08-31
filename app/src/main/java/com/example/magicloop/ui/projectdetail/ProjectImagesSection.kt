package com.example.magicloop.ui.projectdetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            Text("Fotografije", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = {
                pickMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = "Dodaj fotografiju")
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
                    ImageThumbnail(image = image, onDelete = { onDeleteImage(image) })
                }
            }
        }
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
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Ukloni",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(50))
            )
        }
    }
}