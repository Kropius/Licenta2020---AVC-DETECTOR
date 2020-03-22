package com.example.myapplication.util

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor() : Interceptor {

    private val credentials: String

    init {
        this.credentials = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODQ5MTY4MDIsIm5iZiI6MTU4NDkxNjgwMiwianRpIjoiNGMxYzMwMWQtMWI5Zi00ZTdmLThmZjYtOTdmMDQxN2Y4YTczIiwiZXhwIjoxNTg0OTE3NzAyLCJpZGVudGl0eSI6Iktyb3BpdXMiLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.iV4hclHbQZNQx_kPLpwTiw3gOL4_JpgDKnTESoGg0y8"    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
                .header("Authorization","Bearer " + credentials).build()
        return chain.proceed(authenticatedRequest)
    }

}