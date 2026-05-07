package com.university.usersettings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PreferencesContract {
    const val APP_SETTINGS_PREFS = "AppSettings"
    const val PROFILE_PREFS = "ProfilePrefs"

    const val KEY_THEME = "KEY_THEME"
    const val KEY_NOTIFICATIONS = "KEY_NOTIFICATIONS"
    const val KEY_LANGUAGE = "KEY_LANGUAGE"
    const val KEY_FONT_SIZE = "KEY_FONT_SIZE"
    const val KEY_LAST_SAVED = "KEY_LAST_SAVED"
    const val KEY_STUDENT_NAME = "KEY_STUDENT_NAME"
    const val KEY_STUDENT_ID = "KEY_STUDENT_ID"
    const val KEY_DEPARTMENT = "KEY_DEPARTMENT"
    const val KEY_YEAR = "KEY_YEAR"
    const val KEY_EMAIL = "KEY_EMAIL"

    const val DEFAULT_THEME = "light"
    const val DEFAULT_LANGUAGE = "English"
    const val DEFAULT_FONT_SIZE = 16
    const val MIN_FONT_SIZE = 12
    const val MAX_FONT_SIZE = 24
    const val DEFAULT_NOTIFICATIONS = true

    val themeModes = listOf("light", "dark", "system")
    val themeLabels = listOf("Light", "Dark", "System Default")

    fun settingsPrefs(context: Context) = context.getSharedPreferences(APP_SETTINGS_PREFS, Context.MODE_PRIVATE)

    fun profilePrefs(context: Context) = context.getSharedPreferences(PROFILE_PREFS, Context.MODE_PRIVATE)

    fun themeToNightMode(theme: String): Int = when (theme.lowercase(Locale.US)) {
        "dark" -> AppCompatDelegate.MODE_NIGHT_YES
        "system" -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        else -> AppCompatDelegate.MODE_NIGHT_NO
    }

    fun formatTimestamp(epochMillis: Long): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return formatter.format(Date(epochMillis))
    }
}

