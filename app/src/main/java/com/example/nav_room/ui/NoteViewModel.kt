package com.example.nav_room.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nav_room.data.Note
import com.example.nav_room.data.NoteRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // StateFlow for all notes
    val allNotes: StateFlow<List<Note>> = repository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // StateFlow for a single note fetched by ID
    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    // Add a new note
    fun addNote(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    // Update an existing note
    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    // Fetch a note by its ID
    fun getNoteById(noteId: Int) {
        viewModelScope.launch {
            _note.value = repository.getNoteById(noteId)
        }
    }

    // Delete a note by ID
    fun deleteNoteById(noteId: Int) {
        viewModelScope.launch {
            val noteToDelete = repository.getNoteById(noteId)
            noteToDelete?.let { repository.delete(it) }
        }
    }

    // Delete a note (when passing the entire Note object)
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    // Save an image to internal storage and return its URI
    fun saveImageToInternalStorage(bitmap: Bitmap, context: Context): Uri? {
        val filename = "${UUID.randomUUID()}.jpg"
        val file = File(context.filesDir, filename)
        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
