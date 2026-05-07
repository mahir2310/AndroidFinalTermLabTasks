package com.example.studentcoursemanager.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.studentcoursemanager.R
import com.example.studentcoursemanager.model.Course
import com.example.studentcoursemanager.repo.FirebaseCourseRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CourseDetailActivity : AppCompatActivity() {
    private val repo = FirebaseCourseRepository()
    private var mode: String = "add"
    private var course: Course? = null

    // view refs
    private lateinit var etName: TextInputEditText
    private lateinit var etCode: TextInputEditText
    private lateinit var etInstructor: TextInputEditText
    private lateinit var etSchedule: TextInputEditText
    private lateinit var etRoom: TextInputEditText
    private lateinit var spinnerCredits: Spinner
    private lateinit var spinnerSemester: Spinner
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var tilName: TextInputLayout
    private lateinit var tilCode: TextInputLayout
    private lateinit var tilInstructor: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)

        mode = intent.getStringExtra("mode") ?: "add"
        course = intent.getSerializableExtra("course") as? Course

        // bind views
        tilName = findViewById(R.id.tilName)
        tilCode = findViewById(R.id.tilCode)
        tilInstructor = findViewById(R.id.tilInstructor)
        etName = findViewById(R.id.etName)
        etCode = findViewById(R.id.etCode)
        etInstructor = findViewById(R.id.etInstructor)
        spinnerCredits = findViewById(R.id.spinnerCredits)
        etSchedule = findViewById(R.id.etSchedule)
        etRoom = findViewById(R.id.etRoom)
        spinnerSemester = findViewById(R.id.spinnerSemester)
        btnCancel = findViewById(R.id.btnCancel)
        btnSave = findViewById(R.id.btnSave)

        spinnerCredits.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf(1,2,3,4))
        spinnerSemester.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Spring 2025","Summer 2025","Fall 2025"))

        if (mode == "edit" || mode == "view") {
            course?.let { fillFields(it) }
        }

        if (mode == "view") {
            // make fields readonly
            etName.isEnabled = false
            etCode.isEnabled = false
            etInstructor.isEnabled = false
            spinnerCredits.isEnabled = false
            etSchedule.isEnabled = false
            etRoom.isEnabled = false
            spinnerSemester.isEnabled = false
            btnSave.visibility = View.GONE
        }

        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            if (!validate()) return@setOnClickListener
            val c = course ?: Course()
            c.name = etName.text.toString().trim()
            c.code = etCode.text.toString().trim()
            c.instructor = etInstructor.text.toString().trim()
            c.credits = (spinnerCredits.selectedItem as? Int) ?: 1
            c.schedule = etSchedule.text.toString().trim()
            c.room = etRoom.text.toString().trim()
            c.semester = spinnerSemester.selectedItem as? String ?: ""

            if (mode == "add") {
                repo.createCourse(c) { result ->
                    runOnUiThread {
                        result.onSuccess { _ -> Toast.makeText(this, "Course added", Toast.LENGTH_SHORT).show(); finish() }
                        result.onFailure { e -> Toast.makeText(this, "Add failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                    }
                }
            } else {
                repo.updateCourse(c) { result ->
                    runOnUiThread {
                        result.onSuccess { Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show(); finish() }
                        result.onFailure { e -> Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }

        // long-press to delete in edit mode
        if (mode == "edit") {
            btnSave.setOnLongClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete course")
                    .setMessage("Are you sure you want to delete this course?")
                    .setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                        course?.let { existing ->
                            repo.deleteCourse(existing.id) { res ->
                                runOnUiThread {
                                    res.onSuccess { Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show(); finish() }
                                    res.onFailure { e -> Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }

    private fun fillFields(c: Course) {
        etName.setText(c.name)
        etCode.setText(c.code)
        etInstructor.setText(c.instructor)
        spinnerCredits.setSelection(listOf(1,2,3,4).indexOf(c.credits).coerceAtLeast(0))
        etSchedule.setText(c.schedule)
        etRoom.setText(c.room)
        spinnerSemester.setSelection(listOf("Spring 2025","Summer 2025","Fall 2025").indexOf(c.semester).coerceAtLeast(0))
    }

    private fun validate(): Boolean {
        var ok = true
        if (etName.text.toString().trim().isEmpty()) {
            tilName.error = "Required"
            ok = false
        } else tilName.error = null
        if (etCode.text.toString().trim().isEmpty()) {
            tilCode.error = "Required"
            ok = false
        } else tilCode.error = null
        if (etInstructor.text.toString().trim().isEmpty()) {
            tilInstructor.error = "Required"
            ok = false
        } else tilInstructor.error = null
        return ok
    }
}
