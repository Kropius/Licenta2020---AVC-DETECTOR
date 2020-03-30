package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.textResponse
import retrofit2.Response

class TextRepository {
    suspend fun getText(context: Context, token:String):Response<textResponse>{
        return MyApi(context).getText(token)
    }
}