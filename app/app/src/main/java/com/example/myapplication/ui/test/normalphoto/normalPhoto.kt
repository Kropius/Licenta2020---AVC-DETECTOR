package com.example.myapplication.ui.test.normalphoto

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityNormalPhotoBinding
import com.example.myapplication.ui.firstscreen.FirstScreen
import com.example.myapplication.ui.home.home
import com.example.myapplication.ui.test.smilingphoto.smilingPhoto
import com.example.myapplication.util.hide
import com.example.myapplication.util.show
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class normalPhoto : AppCompatActivity(), NormalPhotoListener {


    val multiple_permissions = 101
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_GALLERY = 2
    var viewModel: NormalPhotoViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        val binding: ActivityNormalPhotoBinding = DataBindingUtil.setContentView<ActivityNormalPhotoBinding>(this, com.example.myapplication.R.layout.activity_normal_photo)
        viewModel = ViewModelProviders.of(this).get(NormalPhotoViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel!!.normalPhotoListener = this
        viewModel!!.photoUri = currentPhotoPath
        setupPermissions()

    }

    private fun setupPermissions() {
        var flag: Boolean = false;

        var permissions = java.util.ArrayList<String>(5);
        var permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            flag = true

        } else {
            Log.i("Info", "Permission given!")
        }

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to use camera denied")
            permissions.add(Manifest.permission.CAMERA)
            flag = true

        } else {
            Log.i("Info", "Permission given!")
        }

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to write on external storage denied")
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            flag = true

        } else {
            Log.i("Info", "Permission given!")
        }
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to use internet denied")
            permissions.add(Manifest.permission.INTERNET)
            flag=true

        } else {
            Log.i("Info", "Permission given!")

        }
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("Info", "Permission to record denied")
            permissions.add(Manifest.permission.RECORD_AUDIO)
            flag=true
        } else {
            Log.i("Info", "Permission given!")
        }
        if (flag == true) {
            makeRequest(permissions)
        }
    }

    private fun makeRequest(permission: ArrayList<String>) {
        var myArray: Array<String> = permission.toTypedArray()

        ActivityCompat.requestPermissions(this,
                myArray,
                101)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            multiple_permissions -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {


                } else {
                    Toast.makeText(this,"Permisiuni insuficiente",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, home::class.java))                }
                return
            }
        }
    }
    public fun startGallery(view: View) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
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
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    fun getPath(uri: Uri): String {
        val projection = Array<String>(1) { MediaStore.Images.Media.DATA };
        val cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            galleryAddPic()
            val imgFile = File(currentPhotoPath!!)
            val myBitMap = BitmapFactory.decodeFile(imgFile.absolutePath)
            var myImg = findViewById<ImageView>(R.id.myPhoto)
            myImg.setImageBitmap(myBitMap)
            val layoutParams = myImg.getLayoutParams();
            layoutParams.width = 1400;
            layoutParams.height = 700;
            myImg.setLayoutParams(layoutParams);
            viewModel!!.photoUri = currentPhotoPath


        } else if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (data != null) {
                var ourUri = data.data
                Toast.makeText(this, getPath(ourUri!!), Toast.LENGTH_LONG).show()
                var myImg = findViewById<ImageView>(R.id.myPhoto)
                myImg.setImageURI(ourUri)
                val layoutParams = myImg.getLayoutParams();
                layoutParams.width = 1400;
                layoutParams.height = 700;
                myImg.setLayoutParams(layoutParams);
                viewModel!!.photoUri = getPath(ourUri).toString()


            } else {
                Toast.makeText(this, "INVALID", Toast.LENGTH_LONG).show()
            }
        }
    }

    var currentPhotoPath: String? = null

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

    override fun onStared() {
        progress_bar.show()
        Toast.makeText(this, "Trimitem  poza", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(response: String?) {
        progress_bar.hide()
        Toast.makeText(this, response.toString(), Toast.LENGTH_LONG).show()
        startActivity(Intent(this, smilingPhoto::class.java))
    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        Toast.makeText(this, message.toString(), Toast.LENGTH_LONG).show()

    }
}


