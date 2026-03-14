package com.example.historycalendar.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.historycalendar.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var hour by remember(state.notifyHour) { mutableStateOf(state.notifyHour.toString()) }
    var minute by remember(state.notifyMinute) { mutableStateOf(state.notifyMinute.toString()) }
    var enabled by remember(state.notifyEnabled) { mutableStateOf(state.notifyEnabled) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Đóng") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Thông báo hằng ngày", style = MaterialTheme.typography.titleMedium)
                    Text("Mỗi ngày app sẽ kiểm tra các sự kiện trùng ngày âm hoặc dương và gộp vào một thông báo.", style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bật thông báo", modifier = Modifier.weight(1f))
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = hour, onValueChange = { hour = it.filter(Char::isDigit).take(2) }, modifier = Modifier.weight(1f), label = { Text("Giờ") })
                        OutlinedTextField(value = minute, onValueChange = { minute = it.filter(Char::isDigit).take(2) }, modifier = Modifier.weight(1f), label = { Text("Phút") })
                    }
                    Button(onClick = { viewModel.saveNotification(enabled, hour.toIntOrNull() ?: 7, minute.toIntOrNull() ?: 0) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Lưu cài đặt")
                    }
                }
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sao lưu dữ liệu", style = MaterialTheme.typography.titleMedium)
                    Text("Xuất hoặc nhập JSON để đổi máy mà không mất các ngày lịch sử đã lưu.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export JSON") }
                    Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Import JSON") }
                    state.message?.let { Text(it, color = MaterialTheme.colorScheme.tertiary) }
                }
            }
        }
    }
}
