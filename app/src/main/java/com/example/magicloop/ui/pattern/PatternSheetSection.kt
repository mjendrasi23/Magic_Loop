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
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.ZoomOutMap
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
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

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

    var showDeleteConfirmation by remember { mutableStateOf(false) }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentSheet.pageCount > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(onClick = { viewModel.goToPage(currentPage - 1) }) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = "Prethodna stranica")
                        }
                        Text(
                            text = "Stranica ${currentPage + 1} / ${currentSheet.pageCount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        IconButton(onClick = { viewModel.goToPage(currentPage + 1) }) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Sljedeća stranica")
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ukloni PDF", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Ukloni shemu",
                        modifier = Modifier.size(20.dp)
                    )
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

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Ukloni shemu?") },
            text = { Text("Jeste li sigurni da želite ukloniti PDF shemu iz ovog projekta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePatternSheet()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ukloni")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Odustani")
                }
            }
        )
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

    var scale by remember { mutableStateOf(1f) }
    var viewOffset by remember { mutableStateOf(Offset.Zero) }
    var isDrawMode by remember { mutableStateOf(true) }

    LaunchedEffect(pdfPath, pageIndex, canvasWidthPx) {
        if (canvasWidthPx > 0) {
            pageBitmap = PdfPageRenderer.renderPage(pdfPath, pageIndex, canvasWidthPx)
            canvasHeightPx = pageBitmap?.height ?: 0
        }
    }

    LaunchedEffect(pageIndex) {
        scale = 1f
        viewOffset = Offset.Zero
    }

    var activeStroke by remember(pageIndex) { mutableStateOf<List<Offset>>(emptyList()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = isDrawMode,
                    onClick = { isDrawMode = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = { SegmentedButtonDefaults.Icon(active = isDrawMode) { Icon(Icons.Filled.Edit, null) } }
                ) {
                    Text("Crtaj", style = MaterialTheme.typography.labelSmall)
                }
                SegmentedButton(
                    selected = !isDrawMode,
                    onClick = { isDrawMode = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = { SegmentedButtonDefaults.Icon(active = !isDrawMode) { Icon(Icons.Filled.PanTool, null) } }
                ) {
                    Text("Pomiči", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (scale != 1f || viewOffset != Offset.Zero) {
                IconButton(
                    onClick = {
                        scale = 1f
                        viewOffset = Offset.Zero
                    }
                ) {
                    Icon(
                        Icons.Filled.ZoomOutMap,
                        contentDescription = "Resetiraj zum",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        if (isDrawMode || annotations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isDrawMode) {
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
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (annotations.isNotEmpty()) {
                    TextButton(
                        onClick = onClearPage,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Icon(
                            Icons.Filled.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Obriši bilješke", style = MaterialTheme.typography.labelSmall)
                    }
                }
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
                .pointerInput(isDrawMode) {
                    if (!isDrawMode) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            viewOffset += pan
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = viewOffset.x,
                        translationY = viewOffset.y,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
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
                        .pointerInput(pageIndex, selectedColor, scale, viewOffset, isDrawMode) {
                            if (isDrawMode) {
                                detectDragGestures(
                                    onDragStart = { touchOffset ->
                                        activeStroke = listOf(touchOffset)
                                    },
                                    onDrag = { change, _ ->
                                        activeStroke = activeStroke + change.position
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
}

