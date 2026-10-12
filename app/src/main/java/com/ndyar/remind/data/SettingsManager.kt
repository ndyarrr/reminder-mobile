package com.ndyar.remind.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    var isDailyDefault: Boolean
        get() = prefs.getBoolean("is_daily_default", false)
        set(value) = prefs.edit().putBoolean("is_daily_default", value).apply()

    var isPermanentDefault: Boolean
        get() = prefs.getBoolean("is_permanent_default", false)
        set(value) = prefs.edit().putBoolean("is_permanent_default", value).apply()
}
