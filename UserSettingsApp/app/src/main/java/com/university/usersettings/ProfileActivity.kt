package com.university.usersettings

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvBanner: TextView
    private lateinit var edtStudentId: TextInputEditText
    private lateinit var edtFullName: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var spinnerDepartment: Spinner
    private lateinit var spinnerYear: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarProfile)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindViews()
        setupSpinners()
        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener { saveProfile() }
        toolbar.setNavigationOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        loadProfile()
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
        tvBanner = findViewById(R.id.tvProfileBanner)
        edtStudentId = findViewById(R.id.edtStudentId)
        edtFullName = findViewById(R.id.edtFullName)
        edtEmail = findViewById(R.id.edtEmail)
        spinnerDepartment = findViewById(R.id.spinnerDepartment)
        spinnerYear = findViewById(R.id.spinnerYear)
    }

    private fun setupSpinners() {
        spinnerDepartment.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.department_options,
            android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerYear.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.year_options,
            android.R.layout.simple_spinner_item
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun loadProfile() {
        val prefs = PreferencesContract.profilePrefs(this)
        val name = prefs.getString(PreferencesContract.KEY_STUDENT_NAME, "").orEmpty()
        edtStudentId.setText(prefs.getString(PreferencesContract.KEY_STUDENT_ID, "").orEmpty())
        edtFullName.setText(name)
        edtEmail.setText(prefs.getString(PreferencesContract.KEY_EMAIL, "").orEmpty())

        val department = prefs.getString(PreferencesContract.KEY_DEPARTMENT, "CSE").orEmpty()
        val year = prefs.getString(PreferencesContract.KEY_YEAR, "1st Year").orEmpty()
        spinnerDepartment.setSelection(resources.getStringArray(R.array.department_options).indexOf(department).coerceAtLeast(0))
        spinnerYear.setSelection(resources.getStringArray(R.array.year_options).indexOf(year).coerceAtLeast(0))

        tvBanner.text = getString(R.string.welcome_back, name.ifBlank { "Student" })
    }

    private fun saveProfile() {
        val prefs = PreferencesContract.profilePrefs(this)
        val studentId = edtStudentId.text?.toString().orEmpty().trim()
        val fullName = edtFullName.text?.toString().orEmpty().trim()
        val email = edtEmail.text?.toString().orEmpty().trim()
        val department = spinnerDepartment.selectedItem?.toString().orEmpty().ifBlank { "CSE" }
        val year = spinnerYear.selectedItem?.toString().orEmpty().ifBlank { "1st Year" }

        with(prefs.edit()) {
            putString(PreferencesContract.KEY_STUDENT_ID, studentId)
            putString(PreferencesContract.KEY_STUDENT_NAME, fullName)
            putString(PreferencesContract.KEY_DEPARTMENT, department)
            putString(PreferencesContract.KEY_YEAR, year)
            putString(PreferencesContract.KEY_EMAIL, email)
            apply()
        }

        tvBanner.text = getString(R.string.welcome_back, fullName.ifBlank { "Student" })
    }
}

