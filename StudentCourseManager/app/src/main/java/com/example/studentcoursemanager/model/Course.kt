package com.example.studentcoursemanager.model

import java.io.Serializable

data class Course(
    var id: String = "",
    var name: String = "",
    var code: String = "",
    var instructor: String = "",
    var credits: Int = 1,
    var schedule: String = "",
    var room: String = "",
    var semester: String = "",
    var createdAt: Long = 0L,
    var updatedAt: Long = 0L
) : Serializable

