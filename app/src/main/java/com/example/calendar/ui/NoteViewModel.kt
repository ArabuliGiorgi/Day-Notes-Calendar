package com.example.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calendar.data.Note
import com.example.calendar.data.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

// ViewModel მართავს აპლიკაციის ლოგიკას და მონაცემებს UI-სთვის
class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // არჩეული თარიღის შენახვა (Default-ად დღევანდელი დღე)
    val selectedDate = MutableStateFlow(LocalDate.now())

    // არჩეული თარიღის მიხედვით ჩანაწერების წამოღება ბაზიდან
    @OptIn(ExperimentalCoroutinesApi::class)
    val notesForSelectedDate = selectedDate.flatMapLatest { date ->
        repository.getNotesByDate(date.toString())
    }

    // ყველა ჩანაწერის წამოღება (კალენდარზე ინდიკატორებისთვის)
    val allNotes = repository.getAllNotes()

    // თარიღის შეცვლის ფუნქცია
    fun changeDate(date: LocalDate) {
        selectedDate.value = date
    }

    // ახალი ჩანაწერის დამატება
    fun addNote(title: String, description: String) {
        viewModelScope.launch {
            repository.insertNote(
                Note(
                    title = title,
                    description = description,
                    date = selectedDate.value.toString()
                )
            )
        }
    }

    // ჩანაწერის წაშლა
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }
}
