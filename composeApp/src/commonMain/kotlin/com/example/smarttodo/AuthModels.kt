package com.example.smarttodo

data class User(
    val id: String,
    val email: String,
    val name: String
)

data class AuthUser(
    val name: String,
    val email: String,
    val password: String
)
