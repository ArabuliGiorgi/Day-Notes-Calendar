package com.example.calendar.data

class NoteRepository(private val dao: NoteDao) {

    fun getNotesByDate(date: String) = dao.getNotesByDate(date)

    fun getAllNotes() = dao.getAllNotes()

    suspend fun insertNote(note: Note) {
        dao.insertNote(note)
    }

    suspend fun deleteNote(note: Note) {
        dao.deleteNote(note)
    }
}