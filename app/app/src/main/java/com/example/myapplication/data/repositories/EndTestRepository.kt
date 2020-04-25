package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.endTestResponse
import retrofit2.Response

class EndTestRepository {
    suspend fun endTestRepository(token:String,context: Context): Response<endTestResponse> {
        return MyApi(context).sendFinalResult(token)
    }
}