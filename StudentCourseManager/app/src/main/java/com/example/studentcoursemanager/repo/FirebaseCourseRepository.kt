package com.example.studentcoursemanager.repo

import com.example.studentcoursemanager.model.Course
import com.google.firebase.database.*

class FirebaseCourseRepository : CourseRepository {
    private val database = FirebaseDatabase.getInstance()
    private val coursesRef = database.getReference("courses")

    override fun createCourse(course: Course, callback: (Result<String>) -> Unit) {
        val key = if (course.id.isNotBlank()) course.id else coursesRef.push().key
        if (key == null) {
            callback(Result.failure(Exception("Failed to generate key")))
            return
        }
        val now = System.currentTimeMillis()
        course.id = key
        course.createdAt = now
        course.updatedAt = now
        coursesRef.child(key).setValue(course)
            .addOnSuccessListener {
                callback(Result.success(key))
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }

    override fun listenCourses(callback: (Result<List<Course>>) -> Unit): Any {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Course>()
                for (child in snapshot.children) {
                    val course = child.getValue(Course::class.java)
                    if (course != null) list.add(course)
                }
                callback(Result.success(list))
            }

            override fun onCancelled(error: DatabaseError) {
                callback(Result.failure(Exception(error.message)))
            }
        }
        coursesRef.addValueEventListener(listener)
        return listener
    }

    override fun stopListening(listenerToken: Any) {
        if (listenerToken is ValueEventListener) {
            coursesRef.removeEventListener(listenerToken)
        }
    }

    override fun updateCourse(course: Course, callback: (Result<Unit>) -> Unit) {
        if (course.id.isBlank()) {
            callback(Result.failure(Exception("Course id is blank")))
            return
        }
        course.updatedAt = System.currentTimeMillis()
        coursesRef.child(course.id).setValue(course)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }

    override fun deleteCourse(courseId: String, callback: (Result<Unit>) -> Unit) {
        coursesRef.child(courseId).removeValue()
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e -> callback(Result.failure(e)) }
    }
}

