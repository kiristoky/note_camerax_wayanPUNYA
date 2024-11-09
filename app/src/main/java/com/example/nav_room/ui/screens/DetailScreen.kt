package com.example.nav_room.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.nav_room.data.Note
import com.example.nav_room.ui.NoteViewModel

@Composable
fun DetailScreen(navController: NavController, viewModel: NoteViewModel, noteId: Int?) {
    val context = LocalContext.current // Define context here
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Observe the note if `noteId` is not null
    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.getNoteById(noteId)
        }
    }

    val note = viewModel.note.collectAsState().value
    LaunchedEffect(note) {
        note?.let {
            title = it.title
            content = it.content
            imageUri = it.imageUri?.let { Uri.parse(it) }
        }
    }

    // Camera and gallery launchers
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = viewModel.saveImageToInternalStorage(bitmap, context)
            imageUri = uri
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Display selected image
        imageUri?.let {
            Image(
                painter = rememberImagePainter(data = it),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { cameraLauncher.launch(null) }) {
                Text("Take Photo")
            }
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Pick Image")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save or update note
        Button(
            onClick = {
                val note = Note(
                    id = noteId ?: 0,
                    title = title,
                    content = content,
                    imageUri = imageUri.toString()
                )
                if (noteId == null) {
                    viewModel.addNote(note)
                } else {
                    viewModel.updateNote(note)
                }
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (noteId == null) "Save Note" else "Update Note")
        }

        if (noteId != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.deleteNoteById(noteId)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
            ) {
                Text("Delete Note")
            }
        }
    }
}
