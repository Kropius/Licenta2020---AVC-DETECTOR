package com.example.myapplication.ui.auth

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var username: String? = null
    var password: String? = null
    val email:String?=null

    var normalPhotoUri:String?=null
    var smilingPhotoUri:String?= null
    var registerListener: RegisterListener? = null

    public  fun registerPress(view: View){
            registerListener!!.onStared()
        Toast.makeText(context,username+password+email,Toast.LENGTH_LONG).show()

    }
}