package com.example.spero.helpers

import android.content.Intent
import android.content.Context
import com.example.spero.api.RetrofitClient
import com.example.spero.api.responses.OrdinaryResponse
import com.example.spero.storage.SharedPrefManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun isAuthenticated(context:Context, callback:RetrofitCallback<OrdinaryResponse>){
    val token = SharedPrefManager.getInstance(context).getToken()
}

fun restartActivity(baseContext:Context){
    val intent = baseContext.packageManager
        .getLaunchIntentForPackage(baseContext.packageName)
    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    baseContext.startActivity(intent)
}

fun getErrorMessageFromJSON(json:String):String{
    return JSONObject(json).getString("message")
}
