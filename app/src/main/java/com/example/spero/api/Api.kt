package com.example.spero.api

import com.example.spero.api.requests.EditProfileRequest
import com.example.spero.api.requests.LoginRequest
import com.example.spero.api.requests.RegisterRequest
import com.example.spero.api.responses.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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

    @GET("$usersBase/")
    fun getProfile(): Call<UserResponse>

    @GET("$editBase/")
    fun getAvatar(): Call<AvatarResponse>

    @PUT("$editBase/")
    fun editProfile(@Body request:EditProfileRequest) : Call<OrdinaryResponse>

    @Multipart
    @POST("$editBase/")
    fun editAvatar(@Part avatar: MultipartBody.Part?): Call<OrdinaryResponse>
}
