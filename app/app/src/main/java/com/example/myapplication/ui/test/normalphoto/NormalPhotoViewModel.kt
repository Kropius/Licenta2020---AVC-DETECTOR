package com.example.myapplication.ui.test.normalphoto

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repositories.NormalPhotoRepository
import com.example.myapplication.util.getTokens
import okhttp3.*
import retrofit2.Call
import java.io.File
import retrofit2.Callback
import retrofit2.Response


class NormalPhotoViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var photoUri: String? = null
    var normalPhotoListener: NormalPhotoListener? = null
    fun onNextButtoClick(view: View) {
        //Toast.makeText(context,"We are going to the next step!",Toast.LENGTH_LONG).show()

//        normalPhotoListener?.onStared()
        if (photoUri.isNullOrEmpty()) {
            //Toast.makeText(context,"This is empty",Toast.LENGTH_LONG).show()
            normalPhotoListener?.onFailure("Take or select a photo")

        } else {

                val file = File(photoUri!!)
                val requestFile: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                val requestImage = MultipartBody.Part.createFormData("image", file.name, requestFile)
                Log.i("Info", "cacat"+getTokens(context).toString())
                 NormalPhotoRepository().sendNormalPhoto(getTokens(context),requestImage).enqueue(object:Callback<ResponseBody>{
                     override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                         Toast.makeText(context,t.toString(),Toast.LENGTH_LONG).show()
                     }

                     override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Toast.makeText(context,"matancur"+response.toString(),Toast.LENGTH_LONG).show()
                     }
                 })


            }
//        Toast.makeText(context,"We are going to the next step!",Toast.LENGTH_LONG).show()
            //Coroutines.main()

        }


    }

