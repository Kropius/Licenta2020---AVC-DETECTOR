package com.example.myapplication.ui.auth

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.network.responses.textResponse
import com.example.myapplication.data.repositories.RegisterRepository
import com.example.myapplication.data.repositories.TextRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.getTokens
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var username: String? = null
    var password: String? = null
    var email: String?=null

    var normalPhotoUri:String?=null
    var smilingPhotoUri:String?= null

    var recordingText:String?=null
    var recordingFlag:Boolean = false
    var recordingTextReceivedId:Int?=null
    var recordingTextReceivedText:String?=null


    var typingTextTypedRegister:String?=null
    var typingTextReceivedText:String?=null
    var typingTextReceivedId:Int?=null

    var registerListener: RegisterListener? = null

    public fun getTextTyping(view: View){
        Coroutines.main {
            val textResponse: Response<textResponse> = TextRepository().getText(context, getTokens(context))
            if (textResponse.isSuccessful) {
                typingTextReceivedText= textResponse.body()!!.text
                typingTextReceivedId = textResponse.body()!!.id
                registerListener!!.onSuccesGetTextTyping(typingTextReceivedText)


            } else {
                registerListener!!.onFailureGetTextTyping("Failure, try again!")
            }
        }

    }

    public fun getTextSpeech(view: View){
        Coroutines.main {
            val textResponse: Response<textResponse> = TextRepository().getText(context, getTokens(context))
            if (textResponse.isSuccessful) {

                recordingTextReceivedText = textResponse.body()!!.text
                recordingTextReceivedId = textResponse.body()!!.id
                registerListener!!.onSuccessGetTextRecording(recordingTextReceivedText)

            } else {
               registerListener!!.onFailureGetTextRecording("Failure, try again!")
            }
        }

    }

    public  fun registerPress(view: View){
        Log.i("Info","ciprian"+username)
            registerListener!!.onStared()
        Toast.makeText(context,username,Toast.LENGTH_SHORT).show()
        if(username.isNullOrEmpty()){
           registerListener!!.onFailure("Username invalid!")
            return
        }
        if(password.isNullOrEmpty()){
            registerListener!!.onFailure("Parola invalida!")
            return
        }
        if(email.isNullOrEmpty()){
            registerListener!!.onFailure("Email invalid!")
            return
        }
        if(normalPhotoUri.isNullOrEmpty()){
            registerListener!!.onFailure("Incarca o poza cu tine!")
            return
        }
        if(smilingPhotoUri.isNullOrEmpty()){
            registerListener!!.onFailure("Incarca o poza cu tine zambind!")
            return
        }
        if(recordingFlag==false){
            registerListener!!.onFailure("Inregistrare invalida!")
            return
        }
        if(recordingText.isNullOrEmpty()){
            registerListener!!.onFailure("Inregistrare invalida!")
            return
        }
        if(recordingTextReceivedId == null){
            registerListener!!.onFailure("Nu ai apasat butonul REIMPROSPATEAZA TEXT pentru  inregistrare.")
            return
        }
        if(typingTextReceivedId == null){
            registerListener!!.onFailure("Nu ai apasat butonul REIMPROSPATEAZA TEXT pentru Tastare")
            return
        }
        if(typingTextTypedRegister.isNullOrEmpty()){
            registerListener!!.onFailure("Te rog tasteaza sau ceva si apasa SETEAZA TEXT")
            return
        }
        Coroutines.main {
            val normalPhoto = File(normalPhotoUri!!)
            val normalPhotoRequestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), normalPhoto)
            val requestNormalPhoto = MultipartBody.Part.createFormData("normal_photo", normalPhoto.name, normalPhotoRequestFile)


            val smilingPhoto = File(smilingPhotoUri!!)
            val smilingPhotoRequestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), smilingPhoto)
            val requestSmilingPhoto = MultipartBody.Part.createFormData("smiling_photo", smilingPhoto.name, smilingPhotoRequestFile)

            val regosterResponse = RegisterRepository().register(getTokens(context), context, username!!, password!!, email!!, requestNormalPhoto, requestSmilingPhoto, recordingText!!, recordingTextReceivedId.toString(), typingTextTypedRegister!!, typingTextReceivedId.toString())
            if(regosterResponse.isSuccessful){
                registerListener!!.onSuccess(regosterResponse.body()!!.success)
            }
            else{
                registerListener!!.onFailure("Something went wrong")
            }

        }




    }
}