package com.university.usersettings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var edtStudentName: EditText
    private lateinit var rgTheme: RadioGroup
    private lateinit var rbLight: RadioButton
    private lateinit var rbDark: RadioButton
    private lateinit var rbSystem: RadioButton
    private lateinit var switchNotif: SwitchCompat
    private lateinit var spinnerLang: Spinner
    private lateinit var seekBarFont: SeekBar
    private lateinit var tvFontValue: TextView
    private lateinit var btnSave: View
    private lateinit var btnReset: View
    private lateinit var btnViewSaved: View
    private lateinit var fabProfile: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bindViews()
        setupLanguageSpinner()
        setupFontSeekBar()
        setupActions()
        restoreUiFromPrefs()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        restoreUiFromPrefs()
    }

    private fun bindViews() {
        edtStudentName = findViewById(R.id.edtStudentName)
        rgTheme = findViewById(R.id.rgTheme)
        rbLight = findViewById(R.id.rbLight)
        rbDark = findViewById(R.id.rbDark)
        rbSystem = findViewById(R.id.rbSystem)
        switchNotif = findViewById(R.id.switchNotif)
        spinnerLang = findViewById(R.id.spinnerLang)
        seekBarFont = findViewById(R.id.seekBarFont)
        tvFontValue = findViewById(R.id.tvFontValue)
        btnSave = findViewById(R.id.btnSave)
        btnReset = findViewById(R.id.btnReset)
        btnViewSaved = findViewById(R.id.btnViewSaved)
        fabProfile = findViewById(R.id.fabProfile)
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.language_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLang.adapter = adapter
    }

    private fun setupFontSeekBar() {
        seekBarFont.max = PreferencesContract.MAX_FONT_SIZE - PreferencesContract.MIN_FONT_SIZE
        seekBarFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvFontValue.text = getString(
                    R.string.font_size_format,
                    PreferencesContract.MIN_FONT_SIZE + progress
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun setupActions() {
        btnSave.setOnClickListener { savePreferences() }
        btnReset.setOnClickListener { resetPreferences() }
        btnViewSaved.setOnClickListener {
            startActivity(Intent(this, SettingsViewerActivity::class.java))
        }
        fabProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun selectedTheme(): String = when (rgTheme.checkedRadioButtonId) {
        R.id.rbDark -> "dark"
        R.id.rbSystem -> "system"
        else -> "light"
    }

    private fun restoreUiFromPrefs() {
        val settings = PreferencesContract.settingsPrefs(this)
        val profile = PreferencesContract.profilePrefs(this)

        edtStudentName.setText(profile.getString(PreferencesContract.KEY_STUDENT_NAME, "").orEmpty())

        when (settings.getString(PreferencesContract.KEY_THEME, PreferencesContract.DEFAULT_THEME)) {
            "dark" -> rbDark.isChecked = true
            "system" -> rbSystem.isChecked = true
            else -> rbLight.isChecked = true
        }

        switchNotif.isChecked = settings.getBoolean(
            PreferencesContract.KEY_NOTIFICATIONS,
            PreferencesContract.DEFAULT_NOTIFICATIONS
        )

        val language = settings.getString(
            PreferencesContract.KEY_LANGUAGE,
            PreferencesContract.DEFAULT_LANGUAGE
        ).orEmpty()
        val languageIndex = resources.getStringArray(R.array.language_options).indexOf(language)
        spinnerLang.setSelection(languageIndex.coerceAtLeast(0))

        val fontSize = settings.getInt(
            PreferencesContract.KEY_FONT_SIZE,
            PreferencesContract.DEFAULT_FONT_SIZE
        )
        seekBarFont.progress = (fontSize - PreferencesContract.MIN_FONT_SIZE).coerceIn(
            0,
            PreferencesContract.MAX_FONT_SIZE - PreferencesContract.MIN_FONT_SIZE
        )
        tvFontValue.text = getString(R.string.font_size_format, fontSize)

        AppCompatDelegate.setDefaultNightMode(
            PreferencesContract.themeToNightMode(
                settings.getString(PreferencesContract.KEY_THEME, PreferencesContract.DEFAULT_THEME)
                    .orEmpty()
            )
        )
    }

    private fun savePreferences() {
        val settings = PreferencesContract.settingsPrefs(this)
        val profile = PreferencesContract.profilePrefs(this)
        val name = edtStudentName.text.toString().trim()
        val theme = selectedTheme()
        val notifications = switchNotif.isChecked
        val language = spinnerLang.selectedItem?.toString().orEmpty().ifBlank {
            PreferencesContract.DEFAULT_LANGUAGE
        }
        val fontSize = PreferencesContract.MIN_FONT_SIZE + seekBarFont.progress
        val savedAt = System.currentTimeMillis()

        with(settings.edit()) {
            putString(PreferencesContract.KEY_THEME, theme)
            putBoolean(PreferencesContract.KEY_NOTIFICATIONS, notifications)
            putString(PreferencesContract.KEY_LANGUAGE, language)
            putInt(PreferencesContract.KEY_FONT_SIZE, fontSize)
            putLong(PreferencesContract.KEY_LAST_SAVED, savedAt)
            apply()
        }

        with(profile.edit()) {
            putString(PreferencesContract.KEY_STUDENT_NAME, name)
            apply()
        }

        AppCompatDelegate.setDefaultNightMode(PreferencesContract.themeToNightMode(theme))
        tvFontValue.text = getString(R.string.font_size_format, fontSize)
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun resetPreferences() {
        PreferencesContract.settingsPrefs(this).edit().clear().apply()
        PreferencesContract.profilePrefs(this).edit().clear().apply()

        rbLight.isChecked = true
        switchNotif.isChecked = PreferencesContract.DEFAULT_NOTIFICATIONS
        spinnerLang.setSelection(0)
        seekBarFont.progress = PreferencesContract.DEFAULT_FONT_SIZE - PreferencesContract.MIN_FONT_SIZE
        tvFontValue.text = getString(R.string.font_size_format, PreferencesContract.DEFAULT_FONT_SIZE)
        edtStudentName.text?.clear()
        AppCompatDelegate.setDefaultNightMode(PreferencesContract.themeToNightMode(PreferencesContract.DEFAULT_THEME))

        Toast.makeText(this, "Settings reset to default", Toast.LENGTH_SHORT).show()
    }
}