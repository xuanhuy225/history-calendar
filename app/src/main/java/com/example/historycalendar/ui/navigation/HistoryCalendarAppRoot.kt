package com.example.historycalendar.ui.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.historycalendar.ui.screen.EventFormScreen
import com.example.historycalendar.ui.screen.HomeScreen
import com.example.historycalendar.ui.screen.SettingsScreen
import com.example.historycalendar.ui.screen.TodayEventsScreen
import com.example.historycalendar.ui.viewmodel.EventFormViewModel
import com.example.historycalendar.ui.viewmodel.HomeViewModel
import com.example.historycalendar.ui.viewmodel.SettingsViewModel
import com.example.historycalendar.ui.viewmodel.TodayEventsViewModel
import kotlinx.coroutines.launch

@Composable
fun HistoryCalendarAppRoot(isReady: Boolean) {
    if (!isReady) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CircularProgressIndicator()
                Text("Đang khởi tạo dữ liệu...")
            }
        }
        return
    }

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = AppDestination.Home) {
        composable(AppDestination.Home) {
            val vm: HomeViewModel = hiltViewModel()
            HomeScreen(
                viewModel = vm,
                onAddEvent = { navController.navigate(AppDestination.AddEvent) },
                onOpenToday = { navController.navigate(AppDestination.Today) },
                onOpenDay = { date -> navController.navigate(AppDestination.dayEvents(date.toString())) },
                onOpenSettings = { navController.navigate(AppDestination.Settings) },
                onEditEvent = { navController.navigate("edit_event/$it") }
            )
        }
        composable(AppDestination.AddEvent) {
            val vm: EventFormViewModel = hiltViewModel()
            EventFormScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(
            route = AppDestination.EditEvent,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            val vm: EventFormViewModel = hiltViewModel()
            EventFormScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(AppDestination.Today) {
            val vm: TodayEventsViewModel = hiltViewModel()
            TodayEventsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(
            route = AppDestination.DayEvents,
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) {
            val vm: TodayEventsViewModel = hiltViewModel()
            TodayEventsScreen(viewModel = vm, onBack = { navController.popBackStack() })
        }
        composable(AppDestination.Settings) {
            val vm: SettingsViewModel = hiltViewModel()
            val exportLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.CreateDocument("application/json")
            ) { uri: Uri? ->
                if (uri != null) scope.launch { vm.exportBackup(uri) }
            }
            val importLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri != null) scope.launch { vm.importBackup(uri) }
            }
            SettingsScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() },
                onExport = { exportLauncher.launch("history-calendar-backup.json") },
                onImport = { importLauncher.launch(arrayOf("application/json")) }
            )
        }
    }
}
