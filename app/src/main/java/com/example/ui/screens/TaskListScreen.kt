package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.InstagramTask
import com.example.data.model.TaskStatus
import com.example.ui.theme.InstaOrange
import com.example.ui.theme.InstaPink
import com.example.ui.theme.InstaPurple
import com.example.ui.theme.PrCardDark
import com.example.ui.theme.PrNavyDark
import com.example.ui.theme.PrPrimary
import com.example.ui.theme.PrSuccess
import com.example.ui.theme.PrSurfaceDark
import com.example.ui.theme.PrTextPrimary
import com.example.ui.theme.PrTextSecondary
import com.example.ui.theme.PrWarning

@Composable
fun TaskListScreen(
    tasks: List<InstagramTask>,
    activeTaskId: String?,
    onSelectTask: (InstagramTask) -> Unit,
    onAddTask: (username: String, points: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var filterStatus by remember { mutableStateOf<TaskStatus?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredTasks = tasks.filter { filterStatus == null || it.status == filterStatus }

    val instagramGradient = Brush.horizontalGradient(
        colors = listOf(InstaPurple, InstaPink, InstaOrange)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PrNavyDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                color = PrSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ইনস্টাগ্রাম টাস্ক ক্যাম্পেইন",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )
                    Text(
                        text = "আইডি ফলো করে ইনস্ট্যান্ট স্ক্রিনশট ও সার্ভার জমা নিন",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = filterStatus == null,
                            onClick = { filterStatus = null },
                            label = { Text("সব (${tasks.size})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filterStatus == TaskStatus.PENDING,
                            onClick = { filterStatus = TaskStatus.PENDING },
                            label = { Text("অপেক্ষমাণ (${tasks.count { it.status == TaskStatus.PENDING }})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrWarning,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = filterStatus == TaskStatus.SUBMITTED,
                            onClick = { filterStatus = TaskStatus.SUBMITTED },
                            label = { Text("জমা হয়েছে (${tasks.count { it.status == TaskStatus.SUBMITTED }})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrSuccess,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Task List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        isActive = task.id == activeTaskId,
                        onClick = { onSelectTask(task) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }

        // FAB to add new Instagram target
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab"),
            containerColor = InstaPink,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "নতুন ইনস্টাগ্রাম আইডি যোগ করুন")
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        var usernameInput by remember { mutableStateOf("") }
        var pointsInput by remember { mutableStateOf("50") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PrSurfaceDark)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "নতুন ইনস্টাগ্রাম টাস্ক যোগ করুন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("ইনস্টাগ্রাম ইউজারনেম (উদাঃ cristiano)") },
                        placeholder = { Text("username_without_@") },
                        prefix = { Text("@ ") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InstaPink,
                            unfocusedBorderColor = PrCardDark,
                            focusedTextColor = PrTextPrimary,
                            unfocusedTextColor = PrTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_task_username_input")
                    )

                    OutlinedTextField(
                        value = pointsInput,
                        onValueChange = { pointsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("রিওয়ার্ড পয়েন্ট") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = InstaPink,
                            unfocusedBorderColor = PrCardDark,
                            focusedTextColor = PrTextPrimary,
                            unfocusedTextColor = PrTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showAddDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("বাতিল", color = PrTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (usernameInput.isNotBlank()) {
                                    val pts = pointsInput.toIntOrNull() ?: 50
                                    onAddTask(usernameInput.trim(), pts)
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = InstaPink),
                            modifier = Modifier.testTag("confirm_add_task_button")
                        ) {
                            Text("যোগ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: InstagramTask,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("task_item_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = if (isActive) PrCardDark else PrSurfaceDark),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 6.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Instagram Avatar / Icon Placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(InstaPurple, InstaPink, InstaOrange)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.targetUsername.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@${task.targetUsername}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrTextPrimary
                    )
                    if (isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "সক্রিয়",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = task.instructions,
                    style = MaterialTheme.typography.bodySmall,
                    color = PrTextSecondary,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    when (task.status) {
                        TaskStatus.PENDING -> {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = PrWarning, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("অপেক্ষমাণ", style = MaterialTheme.typography.labelSmall, color = PrWarning)
                        }
                        TaskStatus.IN_PROGRESS -> {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("অটো মেশিনে লোড করা", style = MaterialTheme.typography.labelSmall, color = PrPrimary)
                        }
                        TaskStatus.SUBMITTED, TaskStatus.VERIFIED -> {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সার্ভারে জমা সম্পন্ন", style = MaterialTheme.typography.labelSmall, color = PrSuccess)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "+${task.rewardPoints} Pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = InstaOrange
                    )
                }
            }

            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "মেশিনে চালু করুন",
                tint = if (isActive) InstaPink else PrTextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
