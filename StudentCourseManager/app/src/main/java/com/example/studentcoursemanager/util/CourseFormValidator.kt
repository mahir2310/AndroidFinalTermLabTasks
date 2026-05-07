package com.example.studentcoursemanager.util

data class CourseFormValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String>
)

object CourseFormValidator {
    fun validate(
        name: String,
        code: String,
        instructor: String,
        schedule: String,
        room: String,
        semester: String
    ): CourseFormValidationResult {
        val errors = linkedMapOf<String, String>()

        if (name.trim().isEmpty()) errors["name"] = "Required"
        if (code.trim().isEmpty()) errors["code"] = "Required"
        if (instructor.trim().isEmpty()) errors["instructor"] = "Required"
        if (schedule.trim().isEmpty()) errors["schedule"] = "Required"
        if (room.trim().isEmpty()) errors["room"] = "Required"
        if (semester.trim().isEmpty()) errors["semester"] = "Required"

        return CourseFormValidationResult(errors.isEmpty(), errors)
    }
}

