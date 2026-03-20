package com.example.historycalendar.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.historycalendar.ui.viewmodel.CalendarCellUi
import com.example.historycalendar.ui.viewmodel.HomeViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddEvent: () -> Unit,
    onOpenToday: () -> Unit,
    onOpenDay: (LocalDate) -> Unit,
    onOpenSettings: () -> Unit,
    onEditEvent: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Lịch Lịch Sử",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEvent) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Month header card
            item {
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Hôm nay có gì đáng nhớ?",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Text(
                            state.currentMonthLabel,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = viewModel::previousMonth) { Text("← Trước") }
                            TextButton(onClick = viewModel::nextMonth) { Text("Sau →") }
                            TextButton(onClick = onOpenToday) { Text("Hôm nay") }
                        }
                    }
                }
            }

            // Summary row
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text("Sự kiện đang lưu", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "${state.events.size} mục",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Calendar grid
            item {
                CalendarMonthGrid(monthCells = state.monthCells, onDayClick = onOpenDay)
            }

            // Search
            item {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.search,
                    onValueChange = viewModel::updateSearch,
                    shape = RoundedCornerShape(14.dp),
                    label = { Text("Tìm sự kiện") },
                    placeholder = { Text("Ví dụ: Quốc khánh, chiến thắng...") }
                )
            }

            // Section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Danh sách sự kiện", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${state.events.size} mục", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                }
            }

            // Event list
            items(state.events, key = { it.id }) { event ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditEvent(event.id) },
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(event.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(event.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.tertiary)
                        if (event.description.isNotBlank()) {
                            Text(event.description, style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(
                            onClick = { viewModel.deleteEvent(event.id) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Xóa", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(monthCells: List<CalendarCellUi>, onDayClick: (LocalDate) -> Unit) {
    val weekDays = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
    ElevatedCard(shape = RoundedCornerShape(20.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Weekday header — 7 equal columns, no wrap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                weekDays.forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        lineHeight = 12.sp,
                        maxLines = 1,
                        softWrap = false,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Calendar rows (6 rows × 7 columns = 42 cells)
            val rows = monthCells.chunked(7)
            rows.forEach { rowCells ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Pad incomplete last row
                    val paddedRow = rowCells + List(7 - rowCells.size) { null }
                    paddedRow.forEach { cell ->
                        if (cell == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            CalendarDayCell(
                                cell = cell,
                                modifier = Modifier.weight(1f),
                                onClick = { onDayClick(cell.date) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(cell: CalendarCellUi, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val isOtherMonth = !cell.isCurrentMonth

    // Outer container always the same neutral bg so grid stays stable
    val containerBg = when {
        cell.isToday -> MaterialTheme.colorScheme.primary
        isOtherMonth -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }

    val textColor = when {
        cell.isToday -> MaterialTheme.colorScheme.onPrimary
        isOtherMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    val lunarColor = when {
        cell.isToday -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        isOtherMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.tertiary
    }

    Column(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(containerBg)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp, horizontal = 1.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = cell.solarDay.toString(),
            fontSize = 12.sp,
            fontWeight = if (cell.isToday) FontWeight.ExtraBold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            maxLines = 1
        )
        Text(
            text = cell.lunarText,
            fontSize = 8.sp,
            color = lunarColor,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp,
            maxLines = 1,
            softWrap = false
        )
        if (cell.eventCount > 0) {
            Box(
                modifier = Modifier
                    .padding(top = 1.dp)
                    .background(
                        color = if (cell.isToday) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
                    .padding(horizontal = 3.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (cell.eventCount > 9) "9+" else cell.eventCount.toString(),
                    fontSize = 7.sp,
                    lineHeight = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (cell.isToday) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSecondary,
                    maxLines = 1
                )
            }
        }
    }
}
