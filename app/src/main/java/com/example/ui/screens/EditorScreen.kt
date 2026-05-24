package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: Long,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToNote: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    
    // Auto-save logic
    LaunchedEffect(title, content) {
        if (title.isNotBlank() || content.isNotBlank()) {
            delay(1500) // Debounce 1.5s
            viewModel.saveNote(id = noteId, title = title, content = content)
        }
    }

    LaunchedEffect(noteId) {
        if (noteId != 0L) {
            val note = viewModel.getNoteById(noteId)
            if (note != null) {
                title = note.title
                content = note.content
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        viewModel.saveNote(id = noteId, title = title, content = content)
                        onBack()
                    }) {
                        Text("Simpan")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(48.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    IconButton(onClick = { /* formatting simulation */ }) {
                        Icon(Icons.Filled.FormatBold, contentDescription = "Bold")
                    }
                    IconButton(onClick = { /* list simulation */ }) {
                        Icon(Icons.Filled.FormatListBulleted, contentDescription = "List")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Untitled", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth()
            )
            
            TextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Ketik '@' untuk menyebut catatan lain...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
