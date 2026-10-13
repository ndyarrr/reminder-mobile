package com.ndyar.remind.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ndyar.remind.model.Reminder
import com.ndyar.remind.ui.components.AddReminderDialog
import com.ndyar.remind.ui.components.ReminderItem

import androidx.compose.ui.platform.LocalContext
import com.ndyar.remind.notification.NotificationHelper
import com.ndyar.remind.notification.ReminderScheduler

import androidx.compose.material.icons.filled.Settings
import com.ndyar.remind.data.SettingsManager
import com.ndyar.remind.data.ReminderRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen() {
    val context = LocalContext.current
    val scheduler = remember { ReminderScheduler(context) }
    val settingsManager = remember { SettingsManager(context) }
    val repository = remember { ReminderRepository(context) }
    
    var currentScreen by remember { mutableStateOf("list") }
    
    val reminders = remember {
        mutableStateListOf<Reminder>().apply {
            addAll(repository.getReminders())
        }
    }

    var showDialog by remember { mutableStateOf(false) }
    var editingReminder by remember { mutableStateOf<Reminder?>(null) }

    if (currentScreen == "settings") {
        SettingsScreen(onBack = { currentScreen = "list" })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Reminders") },
                    actions = {
                        IconButton(onClick = { currentScreen = "settings" }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { 
                    editingReminder = null
                    showDialog = true 
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                }
            }
        ) { innerPadding ->
            if (reminders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada tugas untuk dilakukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val uncompletedReminders = reminders.filter { !it.isCompleted }
                val completedReminders = reminders.filter { it.isCompleted }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    if (uncompletedReminders.isNotEmpty()) {
                        item {
                            Text(
                                text = "Belum Selesai",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(uncompletedReminders, key = { it.id }) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onCheckedChange = { isCompleted ->
                                    val index = reminders.indexOf(reminder)
                                    if (index != -1) {
                                        reminders[index] = reminder.copy(isCompleted = isCompleted)
                                        repository.saveReminders(reminders)
                                    }
                                    if (reminder.isPermanent) {
                                        if (isCompleted) {
                                            NotificationHelper(context).cancelNotification(reminder.id)
                                        } else {
                                            NotificationHelper(context).showNotification(
                                                reminder.id,
                                                reminder.title,
                                                reminder.description,
                                                true
                                            )
                                        }
                                    }
                                },
                                onEditClick = {
                                    editingReminder = reminder
                                    showDialog = true
                                },
                                onDeleteClick = {
                                    reminders.remove(reminder)
                                    repository.saveReminders(reminders)
                                    scheduler.cancel(reminder.id)
                                    NotificationHelper(context).cancelNotification(reminder.id)
                                }
                            )
                        }
                    }

                    if (completedReminders.isNotEmpty()) {
                        item {
                            Text(
                                text = "Sudah Selesai",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(completedReminders, key = { it.id }) { reminder ->
                            ReminderItem(
                                reminder = reminder,
                                onCheckedChange = { isCompleted ->
                                    val index = reminders.indexOf(reminder)
                                    if (index != -1) {
                                        reminders[index] = reminder.copy(isCompleted = isCompleted)
                                        repository.saveReminders(reminders)
                                    }
                                    if (reminder.isPermanent) {
                                        if (isCompleted) {
                                            NotificationHelper(context).cancelNotification(reminder.id)
                                        } else {
                                            NotificationHelper(context).showNotification(
                                                reminder.id,
                                                reminder.title,
                                                reminder.description,
                                                true
                                            )
                                        }
                                    }
                                },
                                onEditClick = {
                                    editingReminder = reminder
                                    showDialog = true
                                },
                                onDeleteClick = {
                                    reminders.remove(reminder)
                                    repository.saveReminders(reminders)
                                    scheduler.cancel(reminder.id)
                                    NotificationHelper(context).cancelNotification(reminder.id)
                                }
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AddReminderDialog(
                    reminder = editingReminder,
                    onDismiss = { 
                        showDialog = false
                        editingReminder = null
                    },
                    onConfirm = { title, description, time, deadlineTime ->
                        if (editingReminder != null) {
                            // Update existing reminder
                            val index = reminders.indexOf(editingReminder!!)
                            if (index != -1) {
                                val updatedReminder = editingReminder!!.copy(
                                    title = title,
                                    description = description,
                                    time = time,
                                    deadlineTime = deadlineTime
                                )
                                reminders[index] = updatedReminder
                                repository.saveReminders(reminders)
                                // Cancel old notification and schedule new one
                                scheduler.cancel(updatedReminder.id)
                                scheduler.schedule(updatedReminder)
                            }
                        } else {
                            // Add new reminder
                            val newReminder = Reminder(
                                id = (reminders.maxOfOrNull { it.id } ?: 0) + 1,
                                title = title,
                                description = description,
                                time = time,
                                deadlineTime = deadlineTime,
                                isDaily = settingsManager.isDailyDefault,
                                isPermanent = settingsManager.isPermanentDefault
                            )
                            reminders.add(newReminder)
                            repository.saveReminders(reminders)
                            scheduler.schedule(newReminder)
                        }
                        showDialog = false
                        editingReminder = null
                    }
                )
            }
        }
    }
}
