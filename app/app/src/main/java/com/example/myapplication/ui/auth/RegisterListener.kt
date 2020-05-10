package com.example.myapplication.ui.auth

interface RegisterListener {
    fun onStared()
    fun onSuccess(response:String?)
    fun onFailure(message:String)
    fun onSuccessGetTextRecording(message: String?)
    fun onFailureGetTextRecording(message:String?)

    fun onSuccesGetTextTyping(message: String?)
    fun onFailureGetTextTyping(message:String?)
}