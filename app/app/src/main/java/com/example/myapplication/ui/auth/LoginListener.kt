package com.example.myapplication.ui.auth

import androidx.lifecycle.LiveData

interface LoginListener {
    fun onStared()
    fun onSuccess(response:String?)
    fun onFailure(message:String)
}