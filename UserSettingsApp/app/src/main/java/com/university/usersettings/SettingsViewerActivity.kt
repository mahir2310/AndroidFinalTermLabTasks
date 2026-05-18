package com.university.usersettings

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsViewerActivity : AppCompatActivity() {

    private lateinit var tvNoSettings: TextView
    private lateinit var tvThemeValue: TextView
    private lateinit var tvNotifValue: TextView
    private lateinit var tvLangValue: TextView
    private lateinit var tvFontValue: TextView
    private lateinit var tvNameValue: TextView
    private lateinit var tvLastSaved: TextView
    private lateinit var cardTheme: android.view.View
    private lateinit var cardNotif: android.view.View
    private lateinit var cardLanguage: android.view.View
    private lateinit var cardFont: android.view.View
    private lateinit var cardName: android.view.View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_viewer)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarViewer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
        findViewById<Button>(R.id.btnEdit).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        renderPreferences()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindViews() {
        tvNoSettings = findViewById(R.id.tvNoSettings)
        tvThemeValue = findViewById(R.id.tvThemeValue)
        tvNotifValue = findViewById(R.id.tvNotifValue)
        tvLangValue = findViewById(R.id.tvLangValue)
        tvFontValue = findViewById(R.id.tvFontValue)
        tvNameValue = findViewById(R.id.tvNameValue)
        tvLastSaved = findViewById(R.id.tvLastSaved)
        cardTheme = findViewById(R.id.cardTheme)
        cardNotif = findViewById(R.id.cardNotif)
        cardLanguage = findViewById(R.id.cardLanguage)
        cardFont = findViewById(R.id.cardFont)
        cardName = findViewById(R.id.cardName)
    }

    private fun renderPreferences() {
        val settings = PreferencesContract.settingsPrefs(this)
        val profile = PreferencesContract.profilePrefs(this)
        val lastSaved = settings.getLong(PreferencesContract.KEY_LAST_SAVED, 0L)
        val hasAnySavedSettings = lastSaved != 0L

        tvNoSettings.visibility = if (hasAnySavedSettings) android.view.View.GONE else android.view.View.VISIBLE
        listOf(cardTheme, cardNotif, cardLanguage, cardFont, cardName).forEach {
            it.visibility = if (hasAnySavedSettings) android.view.View.VISIBLE else android.view.View.GONE
        }
        tvLastSaved.visibility = if (hasAnySavedSettings) android.view.View.VISIBLE else android.view.View.GONE

        if (!hasAnySavedSettings) return

        val theme = settings.getString(PreferencesContract.KEY_THEME, PreferencesContract.DEFAULT_THEME).orEmpty()
        val notifications = settings.getBoolean(PreferencesContract.KEY_NOTIFICATIONS, PreferencesContract.DEFAULT_NOTIFICATIONS)
        val language = settings.getString(PreferencesContract.KEY_LANGUAGE, PreferencesContract.DEFAULT_LANGUAGE).orEmpty()
        val fontSize = settings.getInt(PreferencesContract.KEY_FONT_SIZE, PreferencesContract.DEFAULT_FONT_SIZE)
        val studentName = profile.getString(PreferencesContract.KEY_STUDENT_NAME, "").orEmpty().ifBlank { "Not provided" }

        tvThemeValue.text = when (theme.lowercase()) {
            "dark" -> "Dark"
            "system" -> "System Default"
            "light" -> "Light"
            else -> theme.ifBlank { "Light" }
        }
        tvNotifValue.text = if (notifications) "Enabled" else "Disabled"
        tvLangValue.text = language
        tvFontValue.text = getString(R.string.font_size_format, fontSize)
        tvNameValue.text = studentName
        tvLastSaved.text = getString(R.string.last_saved_format, PreferencesContract.formatTimestamp(lastSaved))
    }
}
