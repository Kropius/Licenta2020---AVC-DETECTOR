package com.example.myapplication.ui.test.voiceTest

interface VoiceTestSender {
    fun onStartedSending(string: String)
    fun onSuccessSending(string: String)
    fun onFailureSending(string: String)
}