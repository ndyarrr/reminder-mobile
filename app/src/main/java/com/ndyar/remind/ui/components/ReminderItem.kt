package com.ndyar.remind.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.ndyar.remind.model.Reminder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReminderItem(
    reminder: Reminder,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val deadlineToCompare = reminder.deadlineTime ?: reminder.time
    val isMissed = deadlineToCompare < System.currentTimeMillis() && !reminder.isCompleted
    
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val deadlineFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    
    val formattedTime = remember(reminder.time) { timeFormatter.format(Date(reminder.time)) }
    val formattedDeadline = remember(reminder.deadlineTime) { 
        reminder.deadlineTime?.let { deadlineFormatter.format(Date(it)) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = reminder.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (reminder.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                if (reminder.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reminder.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Notify: $formattedTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (reminder.isDaily) {
                        Text(
                            text = "• Daily",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (reminder.isPermanent) {
                        Text(
                            text = "• Permanent",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    if (formattedDeadline != null && reminder.deadlineTime != reminder.time) {
                        Text(
                            text = "Deadline: $formattedDeadline",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMissed) Color.Red else MaterialTheme.colorScheme.secondary
                        )
                    } else if (isMissed) {
                        Text(
                            text = "Missed!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
                        )
                    }
                }
            }
            
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Reminder",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Reminder",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            if (isMissed) {
                Text(
                    text = "!",
                    color = Color.Red,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
