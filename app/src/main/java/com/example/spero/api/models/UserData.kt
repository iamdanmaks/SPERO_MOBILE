package com.example.spero.api.models

import java.io.Serializable

class UserData(
    val name: String,
    val surname: String,
    val height: Float,
    val weight: Float,
    val username: String,
    val avatar:String
) : Serializable