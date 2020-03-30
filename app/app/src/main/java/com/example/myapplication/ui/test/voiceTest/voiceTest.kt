package com.example.myapplication.ui.test.voiceTest

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySmilingPhotoBinding
import com.example.myapplication.databinding.ActivityVoiceTestBinding
import java.io.IOException

class voiceTest : AppCompatActivity(), VoiceTestListener,VoiceTestSender {
    override fun onStartedSending(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }

    override fun onSuccessSending(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }

    override fun onFailureSending(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }


    override fun onFailure(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_LONG).show()
    }

    override fun onStarted() {
        Toast.makeText(this, "Getting the text!", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        text_speech!!.text = string

    }

    private var fileName: String = ""
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    var mStartRecording = true
    var recordButton: Button? = null
    var viewModel: VoiceTestViewModel? = null
    var mStartPlaying = true
    var playButton: Button? = null
    var text_speech: TextView? = null
    var binding: ActivityVoiceTestBinding? = null
    var REQUEST_RECORD_AUDIO_PERMISSION = 101
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSupportActionBar()?.hide();

        makeRequest()
        binding = DataBindingUtil.setContentView<ActivityVoiceTestBinding>(this, com.example.myapplication.R.layout.activity_voice_test)
        viewModel = ViewModelProviders.of(this).get(VoiceTestViewModel::class.java)
        viewModel!!.voiceListener = this
        viewModel!!.voiceListenerSender=this
        binding!!.viewmodel = viewModel

        text_speech = findViewById(R.id.textToSpeech)
        Toast.makeText(this, text_speech!!.text.toString(), Toast.LENGTH_SHORT).show()
        recordButton = findViewById(R.id.startRecording)
        playButton = findViewById(R.id.startListening)

        fileName = "${externalCacheDir!!.absolutePath}/audiorecordtestxx.wav"
    }

    public fun startStopRecording(view: View) {
        if (mStartRecording == true) {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(fileName)
                setAudioSamplingRate(16000)
                setAudioChannels(1)
                setAudioEncoder(AudioFormat.ENCODING_PCM_16BIT)


                try {
                    prepare()
                } catch (e: IOException) {
                    Log.e("Info", "prepare() failed")
                }

                start()
            }
            mStartRecording = false
        } else {
            stopRecording()
            mStartRecording = true;
            viewModel!!.recordingUri = fileName
        }

    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    public fun startStopPlaying(view: View) {
        if (mStartPlaying == true) {
            player = MediaPlayer().apply {
                try {
                    setDataSource(fileName)
                    prepare()
                    start()
                } catch (e: IOException) {
                    Log.e("Info", "prepare() failed")
                }
            }
            mStartPlaying = false;
        } else {
            stopPlaying()
            mStartPlaying = true
        }
    }

    private fun stopPlaying() {
        player?.release()
        player = null
    }
}
