package com.ndyar.remind.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ndyar.remind.data.ReminderRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.BOOT_COMPLETED") {
            val repository = ReminderRepository(context)
            val scheduler = ReminderScheduler(context)
            val reminders = repository.getReminders()
            val currentTime = System.currentTimeMillis()
            for (reminder in reminders) {
                if (!reminder.isCompleted && reminder.time > currentTime) {
                    scheduler.schedule(reminder)
                }
            }
        }
    }
}
