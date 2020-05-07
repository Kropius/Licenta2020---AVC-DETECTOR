package com.example.myapplication.ui.auth

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
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.ui.firstscreen.FirstScreen
import com.example.myapplication.ui.home.home
import com.example.myapplication.ui.test.normalphoto.normalPhoto
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.io.IOException
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Register : AppCompatActivity(), RegisterListener {

    val multiplePermissions = 101

    val REQUEST_IMAGE_CAPTURE_NORMAL_PHOTO = 1
    val REQUEST_IMAGE_GALLERY_NORMAL_PHOTO = 2

    val REQUEST_IMAGE_CAPTURE_SMILING_PHOTO = 3
    val REQUEST_IMAGE_GALLERY_SMILING_PHOTO = 4


    var normalPhotoPath: String? = null
    var smilingPhotoPath:String?=null

    var viewModel:RegisterViewModel?=null
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
            multiplePermissions -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {


                } else {
                    Toast.makeText(this,"Permisiuni insuficiente",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this, FirstScreen::class.java))                }
                return
            }
        }
    }

    public fun startGalleryRegister(view: View) {
        if(view.id == R.id.start_gallery_normal_photo){
            val photoPickerIntent = Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY_NORMAL_PHOTO);
        }
        else{
            val photoPickerIntent = Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY_SMILING_PHOTO);
        }

    }

    public fun startCameraRegister(view: View) {
        if (view.id == R.id.start_camera_normal_photo) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile("NORMAL_PHOTO")
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
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_NORMAL_PHOTO)
                    }
                }
            }
        }
        else{

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile("SMILING_PHOTO")
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
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_SMILING_PHOTO)
                    }
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

        if (requestCode == REQUEST_IMAGE_CAPTURE_NORMAL_PHOTO && resultCode == AppCompatActivity.RESULT_OK) {
            val imgFile = File(normalPhotoPath!!)
            val myBitMap = BitmapFactory.decodeFile(imgFile.absolutePath)
            var myImg = findViewById<ImageView>(R.id.normal_photo_register)
            myImg.setImageBitmap(myBitMap)
            val layoutParams = myImg.getLayoutParams();
            layoutParams.width = 1400;
            layoutParams.height = 700;
            myImg.setLayoutParams(layoutParams);
            viewModel!!.normalPhotoUri = normalPhotoPath


        } else if (requestCode == REQUEST_IMAGE_GALLERY_NORMAL_PHOTO) {
            if (data != null) {
                var ourUri = data.data
                Toast.makeText(this, getPath(ourUri!!), Toast.LENGTH_LONG).show()
                var myImg = findViewById<ImageView>(R.id.normal_photo_register)
                myImg.setImageURI(ourUri)
                val layoutParams = myImg.getLayoutParams();
                layoutParams.width = 1400;
                layoutParams.height = 700;
                myImg.setLayoutParams(layoutParams);
                Toast.makeText(this, getPath(ourUri), Toast.LENGTH_LONG).show()
                viewModel!!.normalPhotoUri = getPath(ourUri).toString()


            }
        }
            else if (requestCode == REQUEST_IMAGE_GALLERY_SMILING_PHOTO) {
            if (data != null) {
                var ourUri = data.data
                Toast.makeText(this, getPath(ourUri!!), Toast.LENGTH_LONG).show()
                var myImg = findViewById<ImageView>(R.id.smiling_photo_register)
                myImg.setImageURI(ourUri)
                val layoutParams = myImg.getLayoutParams();
                layoutParams.width = 1400;
                layoutParams.height = 700;
                myImg.setLayoutParams(layoutParams);
                Toast.makeText(this, getPath(ourUri), Toast.LENGTH_LONG).show()
                viewModel!!.smilingPhotoUri = getPath(ourUri).toString()


            }
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE_SMILING_PHOTO) {
            if (data != null) {
                val imgFile = File(smilingPhotoPath!!)
                val myBitMap = BitmapFactory.decodeFile(imgFile.absolutePath)
                var myImg = findViewById<ImageView>(R.id.smiling_photo_register)
                myImg.setImageBitmap(myBitMap)
                val layoutParams = myImg.getLayoutParams();
                layoutParams.width = 1400;
                layoutParams.height = 700;
                myImg.setLayoutParams(layoutParams);
                viewModel!!.smilingPhotoUri = smilingPhotoPath


            }
        }
            else {
                Toast.makeText(this, "INVALID", Toast.LENGTH_LONG).show()
            }
        }







    @Throws(IOException::class)
    private fun createImageFile(flag:String): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(flag=="NORMAL_PHOTO") {
            return File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                normalPhotoPath = absolutePath
            }
        }
        else{
            return File.createTempFile(
                    "JPEG_${timeStamp}_", /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                smilingPhotoPath = absolutePath
            }
        }
    }
    override fun onStared() {
        Toast.makeText(this, "Starting to send info for register!", Toast.LENGTH_LONG).show()
    }

    override fun onSuccess(response: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupPermissions()
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        val binding: ActivityRegisterBinding = DataBindingUtil.setContentView<ActivityRegisterBinding>(this, com.example.myapplication.R.layout.activity_register)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel!!.registerListener = this
    }
}
