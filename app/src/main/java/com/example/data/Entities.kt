package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentId: Long? = null,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isProtected: Boolean = false,
    val inTrashSince: Long? = null
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long? = null,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastOpenedAt: Long = System.currentTimeMillis(),
    val inTrashSince: Long? = null
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val _type: String,
    val timestamp: Long = System.currentTimeMillis(),
    val description: String = ""
)
