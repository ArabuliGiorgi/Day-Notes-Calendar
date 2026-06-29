package com.example.calendar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen(
    paddingValues: PaddingValues,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "აპლიკაციის შესახებ",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "DayNotes არის მარტივი კალენდარული ჩანაწერების მართვის აპლიკაცია. " +
                    "მომხმარებელს შეუძლია აირჩიოს დღე კალენდრიდან, დაამატოს ჩანაწერები, " +
                    "ნახოს შენახული ინფორმაცია და მართოს ისინი."
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("მუქი თემა")
                Switch(
                    checked = darkTheme,
                    onCheckedChange = onThemeChange
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("გამოყენებული ტექნოლოგიები:")
        Text("• Kotlin")
        Text("• Jetpack Compose")
        Text("• MVVM არქიტექტურა")
        Text("• Room მონაცემთა ბაზა")
        Text("• Navigation Compose")
        Text("• სპეციალური კალენდრის UI")
        Text("• მუქი/ღია თემის გადამრთველი")
    }
}
