package com.example.studentcoursemanager.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseFormValidatorTest {

    @Test
    fun `validate returns valid when all fields provided`() {
        val result = CourseFormValidator.validate(
            name = "Data Structures",
            code = "CSE301",
            instructor = "Dr. Khan",
            schedule = "Sun-Tue 10:00 AM",
            room = "Room 301",
            semester = "Fall 2025"
        )

        assertTrue(result.isValid)
        assertTrue(result.errors.isEmpty())
    }

    @Test
    fun `validate returns errors for missing fields`() {
        val result = CourseFormValidator.validate("", "", "", "", "", "")

        assertFalse(result.isValid)
        assertTrue(result.errors.containsKey("name"))
        assertTrue(result.errors.containsKey("code"))
        assertTrue(result.errors.containsKey("instructor"))
        assertTrue(result.errors.containsKey("schedule"))
        assertTrue(result.errors.containsKey("room"))
        assertTrue(result.errors.containsKey("semester"))
    }
}

