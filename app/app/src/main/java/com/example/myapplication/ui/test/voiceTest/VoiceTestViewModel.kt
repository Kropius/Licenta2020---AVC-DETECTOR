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
    var voiceListenerSender:VoiceTestSender?=null
    var recordingUri:String?=null
    public fun getTextReq(view: View) {
        voiceListener!!.onStarted()
        Coroutines.main {
            val textResponse: Response<textResponse> = TextRepository().getText(context, getTokens(context))
            if (textResponse.isSuccessful) {

                text = textResponse.body()!!.text
                textId = textResponse.body()!!.id
                voiceListener!!.onSuccess(text!!)

            } else {
                voiceListener!!.onFailure("Failure, try again!")
            }
        }
    }
    public fun sendRecording(view:View){
        voiceListenerSender!!.onStartedSending("Starting to send the registration!")
        if(text.isNullOrEmpty()||textId == null){
            voiceListenerSender!!.onFailureSending("Text is empty. Please refresh the text!")
        }
        if(recordingUri.isNullOrEmpty()){
            voiceListenerSender!!.onFailureSending("Record the text, and then send it")
        }
        Coroutines.main{
            val file = File(recordingUri!!)
            Log.i("Info","dumnezeule"+file.toString())
            val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val requestRecording = MultipartBody.Part.createFormData("recording", file.name, requestFile)
            val id_text_part:RequestBody = RequestBody.create(MediaType.parse("text/plain"),textId!!.toString())
            val response:Response<voiceResponse>
            response = ParseVoiceRepository().sendVoice(getTokens(context),context,requestRecording,id_text_part)
            if(response.isSuccessful){
                Toast.makeText(context,response.body()!!.response,Toast.LENGTH_LONG).show()

            }
            else{
                Toast.makeText(context, "Error",Toast.LENGTH_LONG).show()
            }
        }
    }

}