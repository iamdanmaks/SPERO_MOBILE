package com.example.spero.api.responses

class UserResponse(
    val email: String,
    val username: String,
    val first_name: String,
    val second_name: String,
    val subscriber:Boolean,
    val data_access:Boolean,
    val registered_on:String,
    val date_of_birth:String,
    val height:Float,
    val weight:Float,
    val avatar:Boolean
)