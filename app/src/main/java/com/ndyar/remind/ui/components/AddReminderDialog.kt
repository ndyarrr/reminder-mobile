package com.ndyar.remind.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import com.ndyar.remind.model.Reminder
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderDialog(
    reminder: Reminder? = null,
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, time: Long, deadlineTime: Long?) -> Unit
) {
    var title by remember { mutableStateOf(reminder?.title ?: "") }
    var description by remember { mutableStateOf(reminder?.description ?: "") }
    
    val calendar = remember { 
        Calendar.getInstance().apply {
            reminder?.let { timeInMillis = it.time }
        }
    }
    
    // Notification Time Time state (Time only)
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }
    
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
    )
    
    var showTimePicker by remember { mutableStateOf(false) }

    // Deadline Logic
    var hasDeadline by remember { mutableStateOf(reminder?.deadlineTime != null || reminder == null) }
    val deadlineCalendar = remember {
        Calendar.getInstance().apply {
            reminder?.deadlineTime?.let { timeInMillis = it } ?: run {
                // Default to 1 week (7 days) after notification time if new
                timeInMillis = calendar.timeInMillis + (7L * 24 * 60 * 60 * 1000L)
            }
        }
    }
    
    val deadlineDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = deadlineCalendar.timeInMillis
    )
    var showDeadlineDatePicker by remember { mutableStateOf(false) }
    
    var selectedDeadlineHour by remember { mutableStateOf(deadlineCalendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedDeadlineMinute by remember { mutableStateOf(deadlineCalendar.get(Calendar.MINUTE)) }
    
    val deadlineTimePickerState = rememberTimePickerState(
        initialHour = selectedDeadlineHour,
        initialMinute = selectedDeadlineMinute
    )
    var showDeadlineTimePicker by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Time Picker Dialog for Notification
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    // Deadline Date Picker Dialog
    if (showDeadlineDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDeadlineDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDeadlineDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDeadlineDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = deadlineDatePickerState)
        }
    }

    // Deadline Time Picker Dialog
    if (showDeadlineTimePicker) {
        AlertDialog(
            onDismissRequest = { showDeadlineTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDeadlineHour = deadlineTimePickerState.hour
                    selectedDeadlineMinute = deadlineTimePickerState.minute
                    showDeadlineTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDeadlineTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = deadlineTimePickerState) }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (reminder == null) "Add New Reminder" else "Edit Reminder") },
        text = {
            Column {
                ReminderTextFields(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Notification Time (Jam)", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                    Text(text = formattedTime, style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Set Deadline", modifier = Modifier.weight(1f))
                    Switch(checked = hasDeadline, onCheckedChange = { hasDeadline = it })
                }

                if (hasDeadline) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { showDeadlineDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            val date = Date(deadlineDatePickerState.selectedDateMillis ?: System.currentTimeMillis())
                            Text(text = dateFormat.format(date))
                        }
                        TextButton(
                            onClick = { showDeadlineTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedDeadlineHour, selectedDeadlineMinute)
                            Text(text = formattedTime)
                        }
                    }
                }

                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        val finalCalendar = Calendar.getInstance().apply {
                            reminder?.let { timeInMillis = it.time }
                            set(Calendar.HOUR_OF_DAY, selectedHour)
                            set(Calendar.MINUTE, selectedMinute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val notificationTime = finalCalendar.timeInMillis

                        var deadlineTime: Long? = null
                        if (hasDeadline) {
                            val deadlineSelectedDateMillis = deadlineDatePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            val deadlineUtcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = deadlineSelectedDateMillis
                            }
                            val finalDeadlineCalendar = Calendar.getInstance().apply {
                                set(Calendar.YEAR, deadlineUtcCalendar.get(Calendar.YEAR))
                                set(Calendar.MONTH, deadlineUtcCalendar.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, deadlineUtcCalendar.get(Calendar.DAY_OF_MONTH))
                                set(Calendar.HOUR_OF_DAY, selectedDeadlineHour)
                                set(Calendar.MINUTE, selectedDeadlineMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            deadlineTime = finalDeadlineCalendar.timeInMillis

                            if (deadlineTime < notificationTime) {
                                errorMessage = "Deadline tidak boleh sebelum waktu notifikasi!"
                                return@Button
                            }
                        }
                        
                        errorMessage = null
                        onConfirm(title, description, notificationTime, deadlineTime)
                    }
                }
            ) {
                Text(if (reminder == null) "Add" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReminderTextFields(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
