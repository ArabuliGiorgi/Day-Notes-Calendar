package com.example.calendar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.calendar.ui.NoteViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: NoteViewModel,
    paddingValues: PaddingValues
) {
    // ფოკუსის მართვის მენეჯერი
    val focusManager = LocalFocusManager.current

    // მონაცემების წამოღება ViewModel-იდან
    val selectedDate by viewModel.selectedDate.collectAsState()
    val notes by viewModel.notesForSelectedDate.collectAsState(initial = emptyList())
    val allNotes by viewModel.allNotes.collectAsState(initial = emptyList())

    // ჩანაწერების რაოდენობის დათვლა თითოეული დღისთვის
    val noteCounts = allNotes
        .groupingBy { it.date }
        .eachCount()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
            // ეკრანზე სხვაგან დაჭერისას ფოკუსის მოხსნა
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        item {
            Text(
                text = "DayNotes კალენდარი",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // კალენდრის ზედა ნაწილი (თვეების გადასართველი)
            CalendarHeader(
                currentMonth = currentMonth,
                onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // კალენდრის ბადე (დღეების ჩვენება)
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                noteCounts = noteCounts,
                onDateSelected = { 
                    viewModel.changeDate(it)
                    focusManager.clearFocus() // თარიღის არჩევისას ფოკუსის მოხსნა
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "არჩეული დღე: $selectedDate",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ჩანაწერის სათაურის შეყვანა
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("ჩანაწერის სათაური") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ჩანაწერის აღწერის შეყვანა
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("აღწერა") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ჩანაწერის დამატების ღილაკი
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.addNote(title, description)
                        title = ""
                        description = ""
                        focusManager.clearFocus() // შენახვის შემდეგ ფოკუსის მოხსნა
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ჩანაწერის დამატება")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ჩანაწერები ამ დღისთვის",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (notes.isEmpty()) {
                Text("ამ დღეს ჩანაწერები არ არის.")
            }
        }

        // არჩეული დღის ჩანაწერების სია
        items(notes) { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(note.title, style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(note.description)

                    Spacer(modifier = Modifier.height(10.dp))

                    // ჩანაწერის წაშლის ღილაკი
                    Button(onClick = { viewModel.deleteNote(note) }) {
                        Text("წაშლა")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    // თვის და წლის ჩვენება და ნავიგაცია
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onPreviousMonth) {
            Text("<")
        }

        Text(
            text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
            style = MaterialTheme.typography.titleLarge
        )

        Button(onClick = onNextMonth) {
            Text(">")
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    noteCounts: Map<String, Int>,
    onDateSelected: (LocalDate) -> Unit
) {
    // კვირის დღეების სათაურები
    val daysOfWeek = listOf("ორშ", "სამ", "ოთხ", "ხუთ", "პარ", "შაბ", "კვი")
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()

    val firstWeekDay = firstDayOfMonth.dayOfWeek.value
    val emptyDays = firstWeekDay - 1

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        val totalCells = emptyDays + daysInMonth
        val rows = (totalCells + 6) / 7

        // კალენდრის დღეების გენერაცია
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (column in 0..6) {
                    val cellIndex = row * 7 + column
                    val dayNumber = cellIndex - emptyDays + 1

                    if (dayNumber in 1..daysInMonth) {
                        val date = currentMonth.atDay(dayNumber)
                        val isSelected = date == selectedDate
                        val isToday = date == LocalDate.now()
                        val count = noteCounts[date.toString()] ?: 0

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(3.dp)
                                .background(
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(date) }
                        ) {
                            Text(
                                text = dayNumber.toString(),
                                modifier = Modifier.align(Alignment.Center),
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )

                            // თუ დღეს აქვს ჩანაწერები, ვაჩვენებთ ინდიკატორს
                            if (count > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(18.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.error,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    } else {
                        // ცარიელი უჯრა თვის დასაწყისში/ბოლოში
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        )
                    }
                }
            }
        }
    }
}
