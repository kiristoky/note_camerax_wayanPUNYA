package com.example.nav_room.data

import kotlinx.coroutines.flow.Flow


class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    suspend fun insert(note: Note) = noteDao.insert(note)
    suspend fun update(note: Note) = noteDao.update(note)
    suspend fun delete(note: Note) = noteDao.delete(note)
    suspend fun getNoteById(noteId: Int): Note? = noteDao.getNoteById(noteId)

}
