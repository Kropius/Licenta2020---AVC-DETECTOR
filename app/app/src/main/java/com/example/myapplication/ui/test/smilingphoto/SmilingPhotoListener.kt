package com.example.myapplication.ui.test.smilingphoto

interface SmilingPhotoListener{
    fun onStared()
    fun onSuccess(response:String?)
    fun onFailure(message:String)
}