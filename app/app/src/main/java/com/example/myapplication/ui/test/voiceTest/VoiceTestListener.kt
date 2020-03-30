package com.example.myapplication.ui.test.voiceTest

import com.example.myapplication.databinding.ActivityVoiceTestBinding

interface VoiceTestListener {
    fun onStarted()
    fun onSuccess(string:String)
    fun onFailure(string:String)
}