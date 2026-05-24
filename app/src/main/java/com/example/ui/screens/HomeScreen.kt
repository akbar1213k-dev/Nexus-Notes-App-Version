package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.FolderEntity
import com.example.data.NoteEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToEditor: (Long?) -> Unit,
    onNavigateToGraph: () -> Unit
) {
    val notes by viewModel.activeNotes.collectAsStateWithLifecycle()
    val folders by viewModel.activeFolders.collectAsStateWithLifecycle()
    var showCreateFolder by remember { mutableStateOf(false) }
    var folderName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nexus Notes", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToGraph) {
                        Icon(Icons.Filled.Map, contentDescription = "Graph View")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToEditor(null) }) {
                Icon(Icons.Filled.Add, contentDescription = "New Note")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            
            item {
                Text("TERBARU DIBUKA", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }

            // Show maximum 3 recently opened notes
            items(notes.take(3)) { note ->
                NoteCard(note = note, onClick = { onNavigateToEditor(note.id) }, viewModel)
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("FOLDERS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    TextButton(onClick = { showCreateFolder = true }) {
                        Text("+ New Folder")
                    }
                }
            }

            items(folders) { folder ->
                FolderCard(folder = folder)
            }
            
            item {
                Text("NOTES", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }

            items(notes.drop(3)) { note ->
                NoteCard(note = note, onClick = { onNavigateToEditor(note.id) }, viewModel)
            }
        }

        if (showCreateFolder) {
            AlertDialog(
                onDismissRequest = { showCreateFolder = false },
                title = { Text("Create Folder") },
                text = {
                    OutlinedTextField(
                        value = folderName,
                        onValueChange = { folderName = it },
                        label = { Text("Folder Name") }
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (folderName.isNotBlank()) {
                            viewModel.insertFolder(folderName)
                        }
                        showCreateFolder = false
                        folderName = ""
                    }) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateFolder = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit, viewModel: MainViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(note.title.ifEmpty { "Untitled" }, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FolderCard(folder: FolderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Folder, contentDescription = "Folder", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(folder.name, fontWeight = FontWeight.SemiBold)
        }
    }
}
