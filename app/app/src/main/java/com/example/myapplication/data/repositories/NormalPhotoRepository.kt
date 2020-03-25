package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.normalPhotoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class NormalPhotoRepository {
    suspend fun sendNormalPhoto(token: String, file: MultipartBody.Part, context: Context): Response<normalPhotoResponse>{

        return MyApi(context).uploadNormalPhoto(token, file)

    }
}