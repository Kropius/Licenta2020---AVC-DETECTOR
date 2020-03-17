package com.example.myapplication.data.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.example.myapplication.data.network.MyApi
import com.example.myapplication.data.network.responses.loginResponse
import retrofit2.Response


class UserRepository {
    suspend fun userLogin(email: String, password: String, context: Context): Response<loginResponse> {

        return MyApi().userLogin(email, password)

    }
}
