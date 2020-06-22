package com.example.myapplication.data.network

import android.content.Context
import com.example.myapplication.data.network.responses.*
import com.example.myapplication.data.repositories.NormalPhotoRepository
import com.example.myapplication.ui.test.typingTest.typingTest
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
    suspend fun uploadNormalPhoto(@Header("Bearer") token: String,
                                  @Part file: MultipartBody.Part): Response<normalPhotoResponse>

    @Multipart
    @POST("get_smiley_corners")
    suspend fun uploadSmilingPhoto(@Header("Bearer") token: String,
                                   @Part file: MultipartBody.Part): Response<smilingPhotoResponse>

    @GET("get_text")
    suspend fun getText(@Header("Bearer") token: String): Response<textResponse>

    @Multipart
    @POST("parse_voice")
    suspend fun sendVoice(@Header("Bearer") token: String,
                          @Part file: MultipartBody.Part,
                          @Part("id_text") id_text: RequestBody): Response<voiceResponse>

    @FormUrlEncoded
    @POST("parsevoice")
    suspend fun sendVoiceText(@Header("Bearer") token: String,
                              @Field("recording_id_text") id: String,
                              @Field("text") text: String): Response<voiceResponse>

    @FormUrlEncoded
    @POST("send_texting_test")
    suspend fun sendTextingText(@Header("Bearer") token: String,
                                @Field("id_text") id: String,
                                @Field("input_text") text: String): Response<textingTestResponse>

    @POST("send_final_result")
    suspend fun sendFinalResult(@Header("Bearer") token: String): Response<endTestResponse>

    @Multipart
    @POST("register")
    suspend fun register(@Header("Bearer") token: String,
                                   @Part("username") username: String,
                                   @Part ("password") password: String,
                                   @Part("email") email:String,
                                   @Part normalPhoto: MultipartBody.Part,
                                   @Part smilingPhoto:MultipartBody.Part,
                                   @Part("recording_text") recording:String,
                                   @Part("recording_id_text") recordingIdText:String,
                                   @Part("typed_text") textTyped:String,
                                   @Part("typed_text_id") idTextTyping:String): Response<regosterResponse>
    companion object {
        operator fun invoke(context: Context): MyApi {
            val okHttpClient = OkHttpClient().newBuilder()
                    .addInterceptor(BasicAuthInterceptor(context))
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()
//            return Retrofit.Builder().baseUrl("http://10.0.2.2:5000/")

            return Retrofit.Builder().baseUrl("http://192.168.0.192:80/")
//            return Retrofit.Builder().baseUrl("http://89.137.190.204:80/")

                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
                    .create(MyApi::class.java)
        }
    }
}