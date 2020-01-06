package com.example.spero.api.requests

class RegisterRequest(
    val email: String,
    val password: String,
    val password_repeat: String,
    val first_name: String,
    val second_name: String,
    val username: String,
    val date_of_birth: String,
    val height: Float?,
    val weight: Float?
)
