package com.example.calendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calendar.ui.NoteViewModel

@Composable
fun AllNotesScreen(
    viewModel: NoteViewModel,
    paddingValues: PaddingValues
) {
    // ყველა ჩანაწერის წამოღება ბაზიდან
    val notes by viewModel.allNotes.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "ყველა ჩანაწერი",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Text("ჩანაწერები ჯერ არ გაქვთ შენახული.")
        } else {
            // ყველა ჩანაწერის სია
            LazyColumn {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium)
                            Text(note.description)
                            Text("თარიღი: ${note.date}")
                            Spacer(modifier = Modifier.height(8.dp))
                            // ჩანაწერის წაშლის ღილაკი
                            Button(onClick = { viewModel.deleteNote(note) }) {
                                Text("წაშლა")
                            }
                        }
                    }
                }
            }
        }
    }
}
