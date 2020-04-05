package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.voiceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ParseVoiceRepository {
    suspend fun sendVoiceText(token: String, context: Context, text:String, id_text: String): Response<voiceResponse> {
        return MyApi(context).sendVoiceText(token, id_text, text)
    }
}