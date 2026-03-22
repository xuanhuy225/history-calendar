package com.example.historycalendar.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.historycalendar.data.excel.DuplicateEvent
import com.example.historycalendar.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onImportExcel: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var hour    by remember(state.notifyHour)    { mutableStateOf(state.notifyHour.toString()) }
    var minute  by remember(state.notifyMinute)  { mutableStateOf(state.notifyMinute.toString()) }
    var enabled by remember(state.notifyEnabled) { mutableStateOf(state.notifyEnabled) }

    if (state.pendingDuplicates.isNotEmpty()) {
        DuplicateConflictDialog(
            duplicates      = state.pendingDuplicates,
            onOverwriteAll  = { viewModel.resolveOverwriteAll() },
            onSkipAll       = { viewModel.resolveSkipAll() },
            onResolveSingle = { dup, overwrite -> viewModel.resolveSingle(dup, overwrite) }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Đóng") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Notification settings card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Thông báo hằng ngày", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Mỗi ngày app sẽ kiểm tra các sự kiện trùng ngày âm hoặc dương và gộp vào một thông báo.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Bật thông báo", modifier = Modifier.weight(1f))
                        Switch(checked = enabled, onCheckedChange = { enabled = it })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hour,
                            onValueChange = { hour = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Giờ") }
                        )
                        OutlinedTextField(
                            value = minute,
                            onValueChange = { minute = it.filter(Char::isDigit).take(2) },
                            modifier = Modifier.weight(1f),
                            label = { Text("Phút") }
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.saveNotification(
                                enabled,
                                hour.toIntOrNull() ?: 7,
                                minute.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Lưu cài đặt") }
                }
            }

            // Data import/export card
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Dữ liệu", style = MaterialTheme.typography.titleMedium)
//                    Button(onClick = onExport, modifier = Modifier.fillMaxWidth()) { Text("Export JSON") }
//                    Button(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text("Import JSON") }
                    Button(onClick = onImportExcel, modifier = Modifier.fillMaxWidth()) { Text("Import Excel (.xlsx)") }
                    state.message?.let {
                        Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun DuplicateConflictDialog(
    duplicates: List<DuplicateEvent>,
    onOverwriteAll: () -> Unit,
    onSkipAll: () -> Unit,
    onResolveSingle: (DuplicateEvent, Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* require explicit choice */ },
        title = { Text("Phát hiện ${duplicates.size} sự kiện trùng tên") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Các sự kiện sau đã tồn tại trong database. Chọn cách xử lý:")
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(duplicates, key = { it.existing.id }) { dup ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(dup.existing.title, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "Hiện tại: ${dup.existing.year}-" +
                                    "%02d".format(dup.existing.month) + "-" +
                                    "%02d".format(dup.existing.day) +
                                    "  (${dup.existing.calendarType})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Mới:      ${dup.incoming.year}-" +
                                    "%02d".format(dup.incoming.month) + "-" +
                                    "%02d".format(dup.incoming.day) +
                                    "  (${dup.incoming.calendarType})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                HorizontalDivider()
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { onResolveSingle(dup, false) },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Giữ nguyên") }
                                    Button(
                                        onClick = { onResolveSingle(dup, true) },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Ghi đè") }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onOverwriteAll,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text("Ghi đè tất cả") }
        },
        dismissButton = {
            TextButton(onClick = onSkipAll) { Text("Bỏ qua tất cả") }
        }
    )
}
