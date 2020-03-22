package com.example.myapplication.ui.test.normalphoto

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.internal.bind.TypeAdapters.URI
import java.io.File
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.Image
import android.view.ViewGroup
import android.widget.ImageView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_first.*
import kotlinx.android.synthetic.main.activity_normal_photo.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class normalPhoto : AppCompatActivity() {

    val CAMERA_PERMISSION_REQUEST_CODE = 100;
    val REQUEST_IMAGE_CAPTURE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.myapplication.R.layout.activity_normal_photo)
        setupPermissions()

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to record denied")
            makeRequest()
        } else {
            Log.i("Info", "Permission given!")
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE)
    }

    public fun startCamera(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    Toast.makeText(this,photoURI.toString(),Toast.LENGTH_LONG).show()
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode,resultCode,data)

       if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
           Toast.makeText(this,currentPhotoPath,Toast.LENGTH_LONG).show()
           galleryAddPic()
           val imgFile = File(currentPhotoPath)
           val myBitMap = BitmapFactory.decodeFile(imgFile.absolutePath)
           var myImg = findViewById<ImageView>(R.id.myPhoto)
           myImg.setImageBitmap(myBitMap)
           val layoutParams = myImg.getLayoutParams();
           layoutParams.width = 1400;
           layoutParams.height = 700;
           myImg.setLayoutParams(layoutParams);

       }
    }
    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }
    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            sendBroadcast(mediaScanIntent)
        }
    }
}

