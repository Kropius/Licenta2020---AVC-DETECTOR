package com.example.myapplication.data.repositories

import android.content.Context
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.normalPhotoResponse
import com.example.myapplication.data.network.responses.smilingPhotoResponse
import okhttp3.MultipartBody
import retrofit2.Response

class SmilingPhotoRepository {
    suspend fun sendSmilingPhoto(token: String, file: MultipartBody.Part, context: Context): Response<smilingPhotoResponse> {

        return MyApi(context).uploadSmilingPhoto(token, file)

    }
}