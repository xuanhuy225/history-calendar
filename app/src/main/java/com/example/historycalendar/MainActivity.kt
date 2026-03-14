package com.example.historycalendar

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.historycalendar.ui.navigation.HistoryCalendarAppRoot
import com.example.historycalendar.widget.HistoryCalendarWidgetUpdater
import com.example.historycalendar.ui.theme.HistoryCalendarTheme
import com.example.historycalendar.ui.viewmodel.AppStartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppStartViewModel = hiltViewModel()
            val ready by viewModel.isReady.collectAsStateWithLifecycle()

            LaunchedEffect(ready) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (ready) HistoryCalendarWidgetUpdater.requestUpdate(this@MainActivity)
            }

            HistoryCalendarTheme {
                HistoryCalendarAppRoot(isReady = ready)
            }
        }
    }
}
