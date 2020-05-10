package com.example.myapplication.ui.test.typingTest

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.network.responses.textResponse
import com.example.myapplication.data.network.responses.textingTestResponse
import com.example.myapplication.data.repositories.TextRepository
import com.example.myapplication.data.repositories.TextingTestRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.getTokens
import retrofit2.Response

class TypingTestViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var textId: Int? = null
    var textReceived: String? = null


    var textToBeSent:String?=null
    var typingTestListener: TypingTestListener? = null
    var typingTestListenerSender: TypingTestListenerSender?=null

    public fun getText(view: View) {

        typingTestListener!!.onStarted()
        Coroutines.main {
            val textResponse: Response<textResponse> = TextRepository().getText(context, getTokens(context))
            if(textResponse.isSuccessful){
                textReceived = textResponse.body()!!.text
                textId = textResponse.body()!!.id
                typingTestListener!!.onSuccess(textReceived!!,textId.toString())
            }
        }
    }
    public fun sendText(view:View){
        typingTestListenerSender!!.onStartedSender()
        if(textId == null){
            typingTestListenerSender!!.onFailureSender("Apasa Reimprospateaza textul mai intai!")
            return;
        }
        if(textToBeSent.isNullOrEmpty()){
            typingTestListenerSender!!.onFailureSender("Scrie ceva!")
            return;
        }
        Coroutines.main{
            val response:Response<textingTestResponse> = TextingTestRepository().sendTextingTest(context, getTokens(context),textId.toString()!!,textToBeSent!!)
            if(response.isSuccessful)
                typingTestListenerSender!!.onSuccessSender(response.body()!!.response)
            else{
                typingTestListenerSender!!.onFailureSender("Ceva nu a functionat!")
            }
        }

    }
}