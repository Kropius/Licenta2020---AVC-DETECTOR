package com.example.myapplication.ui.auth

interface RegisterListener {
    fun onStared()
    fun onSuccess(response:String?)
    fun onFailure(message:String)
}