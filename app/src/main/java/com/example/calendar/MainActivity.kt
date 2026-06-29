package com.example.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.calendar.data.NoteDatabase
import com.example.calendar.data.NoteRepository
import com.example.calendar.ui.NoteViewModel
import com.example.calendar.ui.screens.AboutScreen
import com.example.calendar.ui.screens.AllNotesScreen
import com.example.calendar.ui.screens.CalendarScreen
import com.example.calendar.ui.theme.CalendarTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // მონაცემთა ბაზის და რეპოზიტორიის ინიციალიზაცია
        val dao = NoteDatabase.getDatabase(applicationContext).noteDao()
        val repository = NoteRepository(dao)

        // ViewModel-ის შექმნა Factory-ს გამოყენებით
        val viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NoteViewModel(repository) as T
                }
            }
        )[NoteViewModel::class.java]

        setContent {
            // აპლიკაციის თემის მართვა (მუქი/ღია რეჟიმი)
            var darkTheme by remember { mutableStateOf(true) }

            CalendarTheme(darkTheme = darkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CalendarApp(viewModel, darkTheme, onThemeChange = { darkTheme = it })
                }
            }
        }
    }
}

// ქვედა მენიუს ელემენტის მოდელი
data class BottomItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun CalendarApp(
    viewModel: NoteViewModel,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    // ქვედა მენიუს ელემენტების სია
    val items = listOf(
        BottomItem("კალენდარი", "calendar", Icons.Default.DateRange),
        BottomItem("ჩანაწერები", "notes", Icons.Default.List),
        BottomItem("შესახებ", "about", Icons.Default.Info)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            NavigationBar {
                items.forEach { item ->
                    // ვამოწმებთ არის თუ არა მოცემული გვერდი აქტიური
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(item.route) {
                                // ნავიგაციისას ისტორიის მართვა (State-ის შენახვა)
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { padding ->
        // გვერდებს შორის ნავიგაციის მასპინძელი
        NavHost(
            navController = navController,
            startDestination = "calendar"
        ) {
            composable("calendar") {
                CalendarScreen(viewModel, padding)
            }
            composable("notes") {
                AllNotesScreen(viewModel, padding)
            }
            composable("about") {
                AboutScreen(padding, darkTheme, onThemeChange)
            }
        }
    }
}
