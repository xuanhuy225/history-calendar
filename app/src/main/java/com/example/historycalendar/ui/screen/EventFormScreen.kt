package com.example.historycalendar.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.historycalendar.data.db.entity.CalendarType
import com.example.historycalendar.ui.viewmodel.EventFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventFormScreen(
    viewModel: EventFormViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.isEdit) "Sửa sự kiện" else "Thêm sự kiện") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Đóng") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Thông tin sự kiện", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = state.title, onValueChange = viewModel::updateTitle, modifier = Modifier.fillMaxWidth(), label = { Text("Tiêu đề") }, placeholder = { Text("Ví dụ: Tuyên ngôn Độc lập") })
                    OutlinedTextField(value = state.description, onValueChange = viewModel::updateDescription, modifier = Modifier.fillMaxWidth(), minLines = 3, label = { Text("Mô tả") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = state.calendarType == CalendarType.SOLAR, onClick = { viewModel.updateCalendarType(CalendarType.SOLAR) }, label = { Text("Dương lịch") })
                        FilterChip(selected = state.calendarType == CalendarType.LUNAR, onClick = { viewModel.updateCalendarType(CalendarType.LUNAR) }, label = { Text("Âm lịch") })
                    }
                }
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Ngày xảy ra", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = state.day, onValueChange = viewModel::updateDay, modifier = Modifier.weight(1f), label = { Text("Ngày") })
                        OutlinedTextField(value = state.month, onValueChange = viewModel::updateMonth, modifier = Modifier.weight(1f), label = { Text("Tháng") })
                        OutlinedTextField(value = state.year, onValueChange = viewModel::updateYear, modifier = Modifier.weight(1f), label = { Text("Năm") })
                    }
                    OutlinedTextField(value = state.tags, onValueChange = viewModel::updateTags, modifier = Modifier.fillMaxWidth(), label = { Text("Tags") }, placeholder = { Text("Việt Nam, chiến tranh, độc lập") })
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Nhận thông báo", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Switch(checked = state.notifyEnabled, onCheckedChange = viewModel::updateNotifyEnabled)
                    }
                }
            }
            state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(onClick = { viewModel.save(onBack) }, modifier = Modifier.fillMaxWidth()) { Text(if (state.isEdit) "Lưu thay đổi" else "Thêm sự kiện") }
        }
    }
}
