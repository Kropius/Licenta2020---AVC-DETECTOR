package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.regosterResponse
import com.example.myapplication.data.network.responses.voiceResponse
import okhttp3.MultipartBody
import retrofit2.Response

class RegisterRepository {
    suspend fun register(token: String, context: Context, username: String, password: String, email: String, normalPhoto: MultipartBody.Part, smilingPhoto: MultipartBody.Part, recording: String, recordingIdText: String,  textTyped: String,idTextTyping: String): Response<regosterResponse> {
        return MyApi(context).register(token,username,password,email,normalPhoto,smilingPhoto,recording,recordingIdText,textTyped,idTextTyping)
    }
}