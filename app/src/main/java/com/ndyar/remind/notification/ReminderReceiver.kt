package com.ndyar.remind.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ndyar.remind.data.ReminderRepository
import com.ndyar.remind.model.Reminder

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_COMPLETE") {
            val id = intent.getIntExtra("id", -1)
            if (id != -1) {
                val repository = ReminderRepository(context)
                val reminders = repository.getReminders().toMutableList()
                val index = reminders.indexOfFirst { it.id == id }
                if (index != -1) {
                    val reminder = reminders[index]
                    reminders[index] = reminder.copy(isCompleted = true)
                    repository.saveReminders(reminders)
                }
                val notificationHelper = NotificationHelper(context)
                notificationHelper.cancelNotification(id)
            }
            return
        }

        val id = intent.getIntExtra("id", -1)
        if (id == -1) return

        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: ""
        val isDaily = intent.getBooleanExtra("isDaily", false)
        val isPermanent = intent.getBooleanExtra("isPermanent", false)
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(id, title, description, isPermanent)
        
        if (isDaily) {
            val scheduler = ReminderScheduler(context)
            val nextTime = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
            val nextReminder = Reminder(
                id = id,
                title = title,
                description = description,
                time = nextTime,
                isDaily = true,
                isPermanent = isPermanent
            )
            scheduler.schedule(nextReminder)
        }
    }
}
