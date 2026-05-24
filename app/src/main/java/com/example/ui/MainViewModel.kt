package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ActivityEntity
import com.example.data.FolderEntity
import com.example.data.NoteEntity
import com.example.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NotesRepository) : ViewModel() {

    val activeFolders: StateFlow<List<FolderEntity>> = repository.activeFolders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeNotes: StateFlow<List<NoteEntity>> = repository.activeNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.cleanOldTrash()
            // Create default Keterhubungan folder if not exists
            repository.activeFolders.collect { folders ->
                if (folders.none { it.name == "Keterhubungan" && it.isProtected }) {
                    repository.insertFolder(FolderEntity(name = "Keterhubungan", isProtected = true))
                }
            }
        }
    }

    fun insertFolder(name: String, parentId: Long? = null) {
        viewModelScope.launch {
            repository.insertFolder(FolderEntity(name = name, parentId = parentId))
        }
    }

    fun moveToTrashNote(id: Long) {
        viewModelScope.launch {
            repository.moveToTrashNote(id)
        }
    }

    fun moveToTrashFolder(id: Long) {
        viewModelScope.launch {
            repository.moveToTrashFolder(id)
        }
    }

    suspend fun getNoteById(id: Long): NoteEntity? {
        return repository.getNote(id)
    }
    
    suspend fun getNoteByTitle(title: String): NoteEntity? {
        return repository.getNoteByTitle(title)
    }

    fun saveNote(id: Long = 0, folderId: Long? = null, title: String, content: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (id == 0L) {
                repository.insertNote(NoteEntity(folderId = folderId, title = title, content = content, createdAt = now, updatedAt = now, lastOpenedAt = now))
            } else {
                val existing = repository.getNote(id)
                if (existing != null) {
                    repository.updateNote(existing.copy(title = title, content = content, updatedAt = now, lastOpenedAt = now))
                }
            }
        }
    }
}

class MainViewModelFactory(private val repository: NotesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
