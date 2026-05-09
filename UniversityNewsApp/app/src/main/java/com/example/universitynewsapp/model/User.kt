package com.example.universitynewsapp.model

data class Company(val name: String, val catchPhrase: String)

data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val company: Company
)

