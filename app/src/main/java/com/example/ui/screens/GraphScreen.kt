package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NoteEntity
import com.example.ui.MainViewModel
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToEditor: (Long) -> Unit
) {
    val notes by viewModel.activeNotes.collectAsStateWithLifecycle()
    
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mind Map") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offset += pan
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val center = Offset(canvasWidth / 2, canvasHeight / 2) + offset
                
                // Simple circular layout for nodes based on index
                val radius = 300f * scale
                val nodePositions = mutableMapOf<Long, Offset>()
                
                notes.forEachIndexed { index, note ->
                    val angle = (2 * Math.PI * index) / maxOf(1, notes.size)
                    val x = center.x + radius * cos(angle).toFloat()
                    val y = center.y + radius * sin(angle).toFloat()
                    nodePositions[note.id] = Offset(x, y)
                }

                // Draw lines between mentioning notes
                notes.forEach { note ->
                    val startPos = nodePositions[note.id]
                    if (startPos != null) {
                        // Very naive approach: matching @Title
                        notes.forEach { target ->
                            if (note.id != target.id && target.title.isNotBlank() && note.content.contains("@${target.title}")) {
                                val endPos = nodePositions[target.id]
                                if (endPos != null) {
                                    drawLine(
                                        color = Color.LightGray.copy(alpha = 0.6f),
                                        start = startPos,
                                        end = endPos,
                                        strokeWidth = 3f * scale
                                    )
                                }
                            }
                        }
                    }
                }

                // Draw Nodes
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 40f * scale
                    textAlign = android.graphics.Paint.Align.CENTER
                }

                notes.forEach { note ->
                    val pos = nodePositions[note.id]
                    if (pos != null) {
                        drawCircle(
                            color = Color(0xFF3F51B5), // Indigo
                            radius = 40f * scale,
                            center = pos
                        )
                        drawIntoCanvas { canvas ->
                            canvas.nativeCanvas.drawText(
                                note.title.ifEmpty { "Untitled" },
                                pos.x,
                                pos.y + (60f * scale),
                                textPaint
                            )
                        }
                    }
                }
            }
        }
    }
}
