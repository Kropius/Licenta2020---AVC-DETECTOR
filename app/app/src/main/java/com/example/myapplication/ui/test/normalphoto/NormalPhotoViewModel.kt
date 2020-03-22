package com.example.myapplication.ui.test.normalphoto

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class NormalPhotoViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    val photoUri:String? = null

}