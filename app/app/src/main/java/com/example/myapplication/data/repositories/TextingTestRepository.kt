package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.textResponse
import com.example.myapplication.data.network.responses.textingTestResponse
import retrofit2.Response

class TextingTestRepository {
    suspend fun sendTextingTest(context: Context, token:String, id:String, text:String):Response<textingTestResponse>{
        return MyApi(context).sendTextingText(token,id,text)
    }
}