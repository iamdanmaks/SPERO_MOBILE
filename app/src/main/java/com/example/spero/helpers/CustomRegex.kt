package com.example.spero.helpers

class CustomRegex {
    companion object{
        const val USERNAME = """^[a-zA-Z0-9_-]{6,25}$"""
        const val DATE = """^([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$"""
    }
}
