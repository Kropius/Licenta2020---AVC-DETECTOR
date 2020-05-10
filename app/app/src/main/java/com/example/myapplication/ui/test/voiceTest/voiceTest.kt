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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide();


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
                
                        RecognizerIntent.EXTRA_LANGUAGE, "ro"
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
                message = "Dureaza prea mult. Verifica conexiunea la internet. Serverele Google s-ar putea sa nu functioneze. Aplicatia Google trebuie sa fie mereu actualizata."
            }
            if (error == 2) {
                message = "Dureaza prea mult. Verifica conexiunea la internet. Serverele Google s-ar putea sa nu functioneze. Aplicatia Google trebuie sa fie mereu actualizata."
            }
            if (error == 3) {
                message = "Ceva nu a functionat cum trebuie. Reincearca"
            }
            if (error == 4) {
                message = "Dureaza prea mult. Verifica conexiunea la internet. Serverele Google s-ar putea sa nu functioneze. Aplicatia Google trebuie sa fie mereu actualizata."

            }
            if (error == 5) {
                message = "A aparut o problema la telefonul tau. Restarteaza aplicatia.Actualizeaza aplicatia Google, daca este nevoie"
            }
            if (error == 6) {
                message = "Te rog, vorbeste!"
            }
            if (error == 7) {
                message = "Nu am inteles ce ai spus. Mai incearca!"
            }
            if (error == 9) {
                message = "Permisiuni insuficiente!"
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
