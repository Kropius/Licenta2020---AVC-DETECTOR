package com.example.myapplication.util

import  android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class BasicAuthInterceptor(context: Context) : Interceptor {

    private val credentials: String


    init {
        this.credentials = getTokens(context)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val authenticatedRequest = request.newBuilder()
                .header("Authorization", "Bearer " + credentials).build()
        return chain.proceed(authenticatedRequest)
    }

}