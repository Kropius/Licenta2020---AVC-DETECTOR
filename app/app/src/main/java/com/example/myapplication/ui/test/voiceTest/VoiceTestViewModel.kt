package com.example.myapplication.ui.test.voiceTest

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.network.responses.textResponse
import com.example.myapplication.data.network.responses.voiceResponse
import com.example.myapplication.data.repositories.ParseVoiceRepository
import com.example.myapplication.data.repositories.TextRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.getTokens
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class VoiceTestViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var textId: Int? = null
    var text: String? = null

    var voiceListener: VoiceTestListener? = null
    var voiceListenerSender: VoiceTestSender? = null

    var recordingText: String? = null
    var textRecordingFlag: Boolean? = null
    public fun getTextReq(view: View) {
        voiceListener!!.onStarted()
        Coroutines.main {
            val textResponse: Response<textResponse> = TextRepository().getText(context, getTokens(context))
            if (textResponse.isSuccessful) {

                text = textResponse.body()!!.text
                textId = textResponse.body()!!.id
                voiceListener!!.onSuccess(text!!, textId.toString())

            } else {
                voiceListener!!.onFailure("Failure, try again!")
            }
        }
    }

    public fun sendRecording(view: View) {
        voiceListenerSender!!.onStartedSending("Starting to send the registration!")
        if (text.isNullOrEmpty() || textId == null) {
            voiceListenerSender!!.onFailureSending("Text is empty. Please refresh the text!")
            return
        }
        if (recordingText.isNullOrEmpty()) {
            voiceListenerSender!!.onFailureSending("Record the text, and then send it")
            return
        }
        if(textRecordingFlag == false){
            voiceListenerSender!!.onFailureSending("Please give us a proper recording!")
            return
        }
        Coroutines.main {

            var response: Response<voiceResponse> = ParseVoiceRepository().sendVoiceText(getTokens(context), context, recordingText!!, textId.toString())

            if (response.isSuccessful) {
                voiceListenerSender!!.onSuccessSending(response.body()!!.response)
                //Toast.makeText(context, response.body()!!.response, Toast.LENGTH_LONG).show()

            } else {
                voiceListenerSender!!.onFailureSending("Error")
                //Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

}