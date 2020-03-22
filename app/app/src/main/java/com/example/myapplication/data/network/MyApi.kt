package com.example.myapplication.data.network

import com.example.myapplication.data.network.responses.loginResponse
import com.example.myapplication.data.network.responses.normalPhotoResponse
import com.example.myapplication.data.repositories.NormalPhotoRepository
import com.example.myapplication.util.BasicAuthInterceptor
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.PartMap
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


interface MyApi {
    @FormUrlEncoded
    @POST("login")
    suspend fun userLogin(
            @Field("username") username: String,
            @Field("password") password: String
    ): Response<loginResponse>

    @Multipart
    @POST("check_symmetry_normal_img")
    fun uploadNormalPhoto(@Header("Bearer") token:String,
                                      @Part file: MultipartBody.Part): Call<ResponseBody>


    companion object {
        operator fun invoke(): MyApi {
            val okHttpClient = OkHttpClient().newBuilder()
                    .addInterceptor(BasicAuthInterceptor())
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
            return Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")

                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                    .create(MyApi::class.java)
        }
    }
}