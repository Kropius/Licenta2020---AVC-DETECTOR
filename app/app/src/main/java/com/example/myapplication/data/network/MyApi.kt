package com.example.myapplication.data.network

import com.example.myapplication.data.network.responses.loginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface MyApi {
    @FormUrlEncoded
    @POST("login")
    suspend  fun userLogin(
            @Field("username")username:String,
            @Field("password")password:String
    ): Response<loginResponse>

    companion object{
        operator fun invoke() :MyApi{
            return Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MyApi::class.java)
        }
    }
}