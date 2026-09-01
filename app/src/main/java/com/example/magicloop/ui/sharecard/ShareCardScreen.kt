package com.example.magicloop.ui.sharecard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareCardScreen(
    viewModel: ShareCardViewModel,
    onBack: () -> Unit
) {
    val cardData by viewModel.cardData.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kartica za dijeljenje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Natrag")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            val data = cardData
            if (data == null) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        modifier = Modifier.drawWithCache {
                            onDrawWithContent {
                                graphicsLayer.record {
                                    this@onDrawWithContent.drawContent()
                                }
                                drawLayer(graphicsLayer)
                            }
                        }
                    ) {
                        ShareCardContent(data = data)
                    }

                    Button(onClick = {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                            val file = ShareCardExporter.saveBitmapToCache(context, bitmap)
                            ShareCardExporter.shareImage(context, file)
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Podijeli karticu")
                    }
                }
            }
        }
    }
}