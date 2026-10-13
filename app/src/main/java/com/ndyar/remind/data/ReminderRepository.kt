package com.ndyar.remind.data

import android.content.Context
import android.content.SharedPreferences
import com.ndyar.remind.model.Reminder
import org.json.JSONArray
import org.json.JSONObject

class ReminderRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE)

    fun getReminders(): List<Reminder> {
        val jsonString = prefs.getString("reminders_json", null)
        if (jsonString.isNullOrEmpty()) {
            return emptyList()
        }

        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Reminder>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val deadline = obj.getLong("deadlineTime")
                list.add(
                    Reminder(
                        id = obj.getInt("id"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        time = obj.getLong("time"),
                        deadlineTime = if (deadline == -1L) null else deadline,
                        isCompleted = obj.getBoolean("isCompleted"),
                        isDaily = obj.getBoolean("isDaily"),
                        isPermanent = obj.getBoolean("isPermanent")
                    )
                )
            }
            return list
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    fun saveReminders(reminders: List<Reminder>) {
        val jsonArray = JSONArray()
        for (reminder in reminders) {
            val obj = JSONObject().apply {
                put("id", reminder.id)
                put("title", reminder.title)
                put("description", reminder.description)
                put("time", reminder.time)
                put("deadlineTime", reminder.deadlineTime ?: -1L)
                put("isCompleted", reminder.isCompleted)
                put("isDaily", reminder.isDaily)
                put("isPermanent", reminder.isPermanent)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString("reminders_json", jsonArray.toString()).apply()
    }
}
