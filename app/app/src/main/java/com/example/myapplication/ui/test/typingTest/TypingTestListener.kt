package com.example.myapplication.ui.test.typingTest

interface TypingTestListener {
    fun onStarted()
    fun onSuccess(string:String,id_text:String)
    fun onFailure(string:String)
}