package com.example.myapplication.ui.test.voiceTest

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySmilingPhotoBinding
import com.example.myapplication.databinding.ActivityVoiceTestBinding
import com.example.myapplication.ui.test.typingTest.typingTest
import java.io.IOException

class voiceTest : AppCompatActivity(), VoiceTestListener, VoiceTestSender {
    override fun onStartedSending(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    override fun onSuccessSending(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
        startActivity(Intent(this,typingTest::class.java))
    }

    override fun onFailureSending(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }


    override fun onFailure(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    override fun onStarted() {
        Toast.makeText(this, "Getting the text!", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(string: String,id_text:String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        text_speech!!.text = string
        text_speech_id = id_text

    }

    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE: Int = 103
    val INTERNET_PERMISSION = 101
    val RECORD_PERMISSION = 102

    var recordButton: Button? = null

    var text_speech: TextView? = null
    var text_speech_id: String? = null
    var saidText: TextView? = null
    var saidTextFlag = false

    var viewModel: VoiceTestViewModel? = null
    var binding: ActivityVoiceTestBinding? = null
    var REQUEST_RECORD_AUDIO_PERMISSION = 101
    private var sr: SpeechRecognizer? = null

    private fun setupPermissions() {

        var permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to use internet denied")
            makeRequest(Manifest.permission.INTERNET, INTERNET_PERMISSION)

        } else {
            Log.i("Info", "Permission given!")

        }
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to record denied")
            makeRequest(Manifest.permission.RECORD_AUDIO, RECORD_PERMISSION)
        } else {
            Log.i("Info", "Permission given!")
        }
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to write on external storage denied")
            makeRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE_REQUEST_CODE)

        } else {
            Log.i("Info", "Permission given!")

        }
    }

    private fun makeRequest(permission: String, code: Int) {
        ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                code)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide();

        setupPermissions()
        binding = DataBindingUtil.setContentView<ActivityVoiceTestBinding>(this, com.example.myapplication.R.layout.activity_voice_test)
        viewModel = ViewModelProviders.of(this).get(VoiceTestViewModel::class.java)
        viewModel!!.voiceListener = this
        viewModel!!.voiceListenerSender = this
        binding!!.viewmodel = viewModel

        text_speech = findViewById(R.id.textToSpeech)
        saidText = findViewById(R.id.saidText)

        Toast.makeText(this, text_speech!!.text.toString(), Toast.LENGTH_SHORT).show()
        recordButton = findViewById(R.id.startRecording)

        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr!!.setRecognitionListener(listener())


    }

    public fun startStopRecording(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test")

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        sr!!.startListening(intent)

    }


    internal inner class listener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Log.d("Info", "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d("Info", "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d("Info", "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d("Info", "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d("Info", "onEndofSpeech")
        }

        override fun onError(error: Int) {
            saidTextFlag = false
            viewModel!!.textRecordingFlag=false
            Log.d("Info", "error $error")
            var message: String? = null
            if (error == 1) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down.Update the Google App if needed."
            }
            if (error == 2) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down. Update the Google App if needed."
            }
            if (error == 3) {
                message = "There was a problem with the recording."
            }
            if (error == 4) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down.Update the Google App if needed."

            }
            if (error == 5) {
                message = "There was an error with your device. Restart the application.Update the Google App if needed."
            }
            if (error == 6) {
                message = "Please start talking."
            }
            if (error == 7) {
                message = "Couldn't recognize what you said. Please try again."
            }
            if (error == 9) {
                message = "Insufficient permission. Please modify the application's permissions."
            }
            saidText!!.text = message
        }

        override fun onResults(results: Bundle) {
            var str = String()
            Log.d("Info", "onResults $results")
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in data!!.indices) {
                Log.d("Info", "result " + data[i])
                str += data[i]
            }
            saidText!!.text = data[0].toString()
            saidTextFlag = true
            if (saidTextFlag == true) {
                viewModel!!.recordingText = saidText!!.text.toString()
                viewModel!!.textRecordingFlag = true
            }
        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d("Info", "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            Log.d("Info", "onEvent $eventType")
        }
    }

}
