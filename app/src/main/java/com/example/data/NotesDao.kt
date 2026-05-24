package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM folders WHERE inTrashSince IS NULL")
    fun getAllFolders(): Flow<List<FolderEntity>>

    @Query("SELECT * FROM folders WHERE inTrashSince IS NOT NULL")
    fun getTrashedFolders(): Flow<List<FolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: FolderEntity): Long

    @Update
    suspend fun updateFolder(folder: FolderEntity)

    @Query("UPDATE folders SET inTrashSince = :time WHERE id = :id AND isProtected = 0")
    suspend fun moveToTrashFolder(id: Long, time: Long)

    @Query("DELETE FROM folders WHERE inTrashSince IS NOT NULL AND inTrashSince < :cutoff")
    suspend fun cleanOldTrashFolders(cutoff: Long)

    @Query("SELECT * FROM notes WHERE inTrashSince IS NULL ORDER BY lastOpenedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE title = :title LIMIT 1")
    suspend fun getNoteByTitle(title: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE inTrashSince IS NOT NULL")
    fun getTrashedNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET inTrashSince = :time WHERE id = :id")
    suspend fun moveToTrashNote(id: Long, time: Long)

    @Query("DELETE FROM notes WHERE inTrashSince IS NOT NULL AND inTrashSince < :cutoff")
    suspend fun cleanOldTrashNotes(cutoff: Long)
    
    // TractApp
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)
}
