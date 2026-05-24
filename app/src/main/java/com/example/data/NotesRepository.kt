package com.example.data

import kotlinx.coroutines.flow.Flow

class NotesRepository(private val dao: NotesDao) {
    val activeFolders: Flow<List<FolderEntity>> = dao.getAllFolders()
    val activeNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    val activities: Flow<List<ActivityEntity>> = dao.getAllActivities()

    suspend fun insertFolder(folder: FolderEntity) = dao.insertFolder(folder)
    suspend fun updateFolder(folder: FolderEntity) = dao.updateFolder(folder)
    suspend fun moveToTrashFolder(id: Long) = dao.moveToTrashFolder(id, System.currentTimeMillis())
    
    suspend fun getNote(id: Long) = dao.getNoteById(id)
    suspend fun getNoteByTitle(title: String) = dao.getNoteByTitle(title)
    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun moveToTrashNote(id: Long) = dao.moveToTrashNote(id, System.currentTimeMillis())
    
    suspend fun cleanOldTrash() {
        val cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000) // 24 hours
        dao.cleanOldTrashFolders(cutoff)
        dao.cleanOldTrashNotes(cutoff)
    }

    suspend fun insertActivity(activity: ActivityEntity) = dao.insertActivity(activity)
}
