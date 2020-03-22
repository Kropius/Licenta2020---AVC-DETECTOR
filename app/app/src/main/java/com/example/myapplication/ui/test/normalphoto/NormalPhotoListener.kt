package com.example.myapplication.ui.test.normalphoto

interface NormalPhotoListener {
    fun onStared()
    fun onSuccess(response:String?)
    fun onFailure(message:String)
}