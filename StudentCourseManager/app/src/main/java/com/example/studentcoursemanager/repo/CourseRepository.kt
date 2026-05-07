package com.example.studentcoursemanager.repo

import com.example.studentcoursemanager.model.Course

interface CourseRepository {
    fun createCourse(course: Course, callback: (Result<String>) -> Unit)
    fun listenCourses(callback: (Result<List<Course>>) -> Unit): Any
    fun stopListening(listenerToken: Any)
    fun updateCourse(course: Course, callback: (Result<Unit>) -> Unit)
    fun deleteCourse(courseId: String, callback: (Result<Unit>) -> Unit)
}

