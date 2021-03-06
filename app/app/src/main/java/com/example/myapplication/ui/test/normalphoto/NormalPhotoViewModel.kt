package com.example.myapplication.ui.test.normalphoto

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.network.responses.loginResponse
import com.example.myapplication.data.repositories.NormalPhotoRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.getTokens
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class NormalPhotoViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var photoUri: String? = null
    var normalPhotoListener: NormalPhotoListener? = null

    fun onNextButtoClick(view: View) {
        //Toast.makeText(context,"We are going to the next step!",Toast.LENGTH_LONG).show()

        normalPhotoListener?.onStared()
        if (photoUri.isNullOrEmpty()) {
            //Toast.makeText(context,"This is empty",Toast.LENGTH_LONG).show()
            normalPhotoListener?.onFailure("Take or select a photo")

        } else {
            Coroutines.main {
                val file = File(photoUri!!)
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val requestImage = MultipartBody.Part.createFormData("image", file.name, requestFile)
                // Log.i("Info", "cacat"+getTokens(context).toString())
               val normalPhotoRespone =  NormalPhotoRepository().sendNormalPhoto(getTokens(context), requestImage, context)
                if(normalPhotoRespone.isSuccessful)
                {   normalPhotoListener?.onSuccess(normalPhotoRespone.body()?.response)
//                    Toast.makeText(context,normalPhotoRespone.body().toString(),Toast.LENGTH_LONG).show()
                }
                else{
                    normalPhotoListener?.onFailure("Error")
                }
            }

        }
//        Toast.makeText(context,"We are going to the next step!",Toast.LENGTH_LONG).show()
        //Coroutines.main()

    }


}

