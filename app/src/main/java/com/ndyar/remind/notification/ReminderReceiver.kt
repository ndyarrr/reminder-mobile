package com.ndyar.remind.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ndyar.remind.model.Reminder

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
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
