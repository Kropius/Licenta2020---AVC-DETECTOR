package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.voiceResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ParseVoiceRepository {
    suspend fun sendVoice(token: String, context: Context, file: MultipartBody.Part, id_text: RequestBody): Response<voiceResponse> {
        return MyApi(context).sendVoice(token, file, id_text)
    }
}