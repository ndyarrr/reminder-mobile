package com.ndyar.remind.ui.screens

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ndyar.remind.model.Reminder
import com.ndyar.remind.ui.components.AddReminderDialog
import com.ndyar.remind.ui.components.ReminderItem

import androidx.compose.ui.platform.LocalContext
import com.ndyar.remind.notification.NotificationHelper
import com.ndyar.remind.notification.ReminderScheduler
import java.util.Calendar

import androidx.compose.material.icons.filled.Settings
import com.ndyar.remind.data.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderListScreen() {
    val context = LocalContext.current
    val scheduler = remember { ReminderScheduler(context) }
    val settingsManager = remember { SettingsManager(context) }
    
    var currentScreen by remember { mutableStateOf("list") }
    
    val reminders = remember {
        mutableStateListOf(
            Reminder(1, "Buy groceries", "Milk, eggs, bread", Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 17); set(Calendar.MINUTE, 0) }.timeInMillis),
            Reminder(2, "Workout", "Gym session", Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 7); set(Calendar.MINUTE, 0) }.timeInMillis),
            Reminder(3, "Meeting", "Team sync", Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 10); set(Calendar.MINUTE, 0) }.timeInMillis)
        )
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderItem(
                        reminder = reminder,
                        onCheckedChange = { isCompleted ->
                            val index = reminders.indexOf(reminder)
                            if (index != -1) {
                                reminders[index] = reminder.copy(isCompleted = isCompleted)
                            }
                            if (isCompleted && reminder.isPermanent) {
                                NotificationHelper(context).cancelNotification(reminder.id)
                            }
                        },
                        onEditClick = {
                            editingReminder = reminder
                            showDialog = true
                        }
                    )
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
