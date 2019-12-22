package com.example.spero.api

import com.example.spero.api.requests.LoginRequest
import com.example.spero.api.requests.RegisterRequest
import com.example.spero.api.responses.DiagnosticResponse

import com.example.spero.api.responses.LoginResponse
import com.example.spero.api.responses.OrdinaryResponse
import retrofit2.Call
import retrofit2.http.*

const val usersBase = "user"
const val authBase = "account"
const val editBase = "edit"
const val diagnosisBase = "diagnosis"

interface Api {
    @POST("$authBase/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("$usersBase/")
    fun register(
        @Body request: RegisterRequest
    ): Call<LoginResponse>

    @GET("$editBase/reset")
    fun forgotPassword(
        @Query("email") email: String
    ): Call<OrdinaryResponse>

    @GET("$diagnosisBase/")
    fun getDiagnoses(): Call<List<DiagnosticResponse>>
}
