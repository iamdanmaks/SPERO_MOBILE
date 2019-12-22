package com.example.spero.api.responses

class DiagnosticResponse(
    val result: Number,
    val normal_probability: Number,
    val murmur_probability: Number,
    val extrasystole_probability: Number,
    val public_id: String,
    val checked_on: String
)
