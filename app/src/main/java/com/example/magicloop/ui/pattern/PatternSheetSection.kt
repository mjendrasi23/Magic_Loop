package com.example.magicloop.ui.pattern

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.example.magicloop.data.local.entity.PatternAnnotationEntity
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds

private val availableColors = listOf(
    "#E53935", // crvena
    "#1E88E5", // plava
    "#43A047", // zelena
    "#212121"  // crna
)

@Composable
fun PatternSheetSection(viewModel: PatternViewModel) {
    val sheet by viewModel.sheet.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val annotations by viewModel.annotations.collectAsState()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importPdf(it) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (sheet == null) {
            OutlinedButton(
                onClick = { filePickerLauncher.launch(arrayOf("application/pdf")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.UploadFile, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Uvezi shemu (PDF)")
            }
        } else {
            val currentSheet = sheet!!

            if (currentSheet.pageCount > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.goToPage(currentPage - 1) }) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = "Prethodna stranica")
                    }
                    Text(text= "Stranica ${currentPage + 1} / ${currentSheet.pageCount}", style= MaterialTheme.typography.bodyMedium )
                    IconButton(onClick = { viewModel.goToPage(currentPage + 1) }) {
                        Icon(Icons.Filled.ChevronRight, contentDescription = "Sljedeća stranica")
                    }
                }
            }

            PdfDrawingCanvas(
                pdfPath = currentSheet.pdfUriPath,
                pageIndex = currentPage,
                annotations = annotations,
                onStrokeFinished = { points, colorHex ->
                    viewModel.saveStroke(points, colorHex)
                },
                onClearPage = { viewModel.clearPageAnnotations() }
            )
        }
    }
}

@Composable
private fun PdfDrawingCanvas(
    pdfPath: String,
    pageIndex: Int,
    annotations: List<PatternAnnotationEntity>,
    onStrokeFinished: (List<Offset>, String) -> Unit,
    onClearPage: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(availableColors[0]) }
    var canvasWidthPx by remember { mutableStateOf(0) }
    var canvasHeightPx by remember { mutableStateOf(0) }
    var pageBitmap by remember(pdfPath, pageIndex, canvasWidthPx) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(pdfPath, pageIndex, canvasWidthPx) {
        if (canvasWidthPx > 0) {
            pageBitmap = PdfPageRenderer.renderPage(pdfPath, pageIndex, canvasWidthPx)
            canvasHeightPx = pageBitmap?.height ?: 0
        }
    }

    var activeStroke by remember(pageIndex) { mutableStateOf<List<Offset>>(emptyList()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                availableColors.forEach { hex ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color)
                            .border(
                                width = if (selectedColor == hex) 3.dp else 1.dp,
                                color = if (selectedColor == hex) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = RoundedCornerShape(50)
                            )
                            .clickable { selectedColor = hex }
                    )
                }
            }
            IconButton(onClick = onClearPage) {
                Icon(Icons.Filled.Delete, contentDescription = "Obriši oznake na stranici")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(
                    if (canvasHeightPx > 0 && canvasWidthPx > 0)
                        canvasWidthPx.toFloat() / canvasHeightPx
                    else 0.75f
                )
                .background(Color.White)
                .border(1.dp, Color.LightGray)
                .clipToBounds()
                .onSizeChanged { size ->
                    if (size.width != canvasWidthPx) canvasWidthPx = size.width
                }
        ) {
            pageBitmap?.let { bmp ->
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Shema pletenja",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pageIndex, selectedColor) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val clamped = Offset(
                                    offset.x.coerceIn(0f, size.width.toFloat()),
                                    offset.y.coerceIn(0f, size.height.toFloat())
                                )
                                activeStroke = listOf(clamped)
                            },
                            onDrag = { change, _ ->
                                val clamped = Offset(
                                    change.position.x.coerceIn(0f, size.width.toFloat()),
                                    change.position.y.coerceIn(0f, size.height.toFloat())
                                )
                                activeStroke = activeStroke + clamped
                            },
                            onDragEnd = {
                                if (activeStroke.size >= 2 && canvasWidthPx > 0 && canvasHeightPx > 0) {
                                    val normalized = activeStroke.map {
                                        Offset(it.x / canvasWidthPx, it.y / canvasHeightPx)
                                    }
                                    onStrokeFinished(normalized, selectedColor)
                                }
                                activeStroke = emptyList()
                            }
                        )
                    }
            ) {
                annotations.forEach { annotation ->
                    val points = PathEncoding.decode(annotation.pathData)
                    if (points.size >= 2) {
                        val path = Path()
                        val first = points.first()
                        path.moveTo(first.x * size.width, first.y * size.height)
                        points.drop(1).forEach { p ->
                            path.lineTo(p.x * size.width, p.y * size.height)
                        }
                        drawPath(
                            path = path,
                            color = Color(android.graphics.Color.parseColor(annotation.colorHex)),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = annotation.strokeWidth
                            )
                        )
                    }
                }

                if (activeStroke.size >= 2) {
                    val path = Path()
                    path.moveTo(activeStroke.first().x, activeStroke.first().y)
                    activeStroke.drop(1).forEach { path.lineTo(it.x, it.y) }
                    drawPath(
                        path = path,
                        color = Color(android.graphics.Color.parseColor(selectedColor)),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                    )
                }
            }
        }
    }
}

