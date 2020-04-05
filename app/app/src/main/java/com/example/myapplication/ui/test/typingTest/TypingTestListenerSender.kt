package com.example.myapplication.ui.test.typingTest

interface TypingTestListenerSender {
    fun onStartedSender()
    fun onSuccessSender(string:String)
    fun onFailureSender(string:String)
}