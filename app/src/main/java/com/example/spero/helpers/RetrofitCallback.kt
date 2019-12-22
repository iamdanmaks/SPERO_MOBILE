package com.example.spero.helpers

import okhttp3.ResponseBody


interface RetrofitCallback<T> {
    fun onSuccess(value: T)
    fun onServerError(error:ResponseBody)
    fun onError(throwable: Throwable)
}
