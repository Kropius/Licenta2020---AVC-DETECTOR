package com.example.myapplication.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.example.myapplication.util.toast
import kotlinx.android.synthetic.main.activity_register.*
import org.w3c.dom.Text
import java.io.File
import java.io.IOException
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Register : AppCompatActivity(), RegisterListener {
    override fun onFailureGetTextTyping(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuccesGetTextTyping(message: String?) {
       textReceivedTypingRegister!!.text= message
    }

    override fun onSuccessGetTextRecording(message: String?) {
            textReceivedSpeechRegister!!.text = message
    }

    override fun onFailureGetTextRecording(message: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    override fun onStared() {
        Toast.makeText(this, "Starting to send info for register!", Toast.LENGTH_LONG).show()
    }

    override fun onSuccess(response: String?) {
        toast(response!!)
    }

    override fun onFailure(message: String) {
        toast(message)
    }
    val multiplePermissions = 101

    val REQUEST_IMAGE_CAPTURE_NORMAL_PHOTO = 1
    val REQUEST_IMAGE_GALLERY_NORMAL_PHOTO = 2

    val REQUEST_IMAGE_CAPTURE_SMILING_PHOTO = 3
    val REQUEST_IMAGE_GALLERY_SMILING_PHOTO = 4

    var sr:SpeechRecognizer?=null
    var normalPhotoPath: String? = null
    var smilingPhotoPath:String?=null

    var saidText:TextView?=null
    var saidTextFlag = false
    var textReceivedSpeechRegister:TextView?=null

    var typedText:TextView?=null
    var typedTextEditText:EditText?=null
    var textReceivedTypingRegister:TextView?=null

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

    public fun startStopRecordingRegister(view: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(

                RecognizerIntent.EXTRA_LANGUAGE, "ro"
        )
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test")

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        sr!!.startListening(intent)

    }


    internal inner class listener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            Log.d("Info", "onReadyForSpeech")
        }

        override fun onBeginningOfSpeech() {
            Log.d("Info", "onBeginningOfSpeech")
        }

        override fun onRmsChanged(rmsdB: Float) {
            Log.d("Info", "onRmsChanged")
        }

        override fun onBufferReceived(buffer: ByteArray) {
            Log.d("Info", "onBufferReceived")
        }

        override fun onEndOfSpeech() {
            Log.d("Info", "onEndofSpeech")
        }

        override fun onError(error: Int) {
            saidTextFlag = false
            //viewModel!!.textRecordingFlag=false
            Log.d("Info", "error $error")
            var message: String? = null
            if (error == 1) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down.Update the Google App if needed."
            }
            if (error == 2) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down. Update the Google App if needed."
            }
            if (error == 3) {
                message = "There was a problem with the recording."
            }
            if (error == 4) {
                message = "Operation timed out. Check your internet connection. Google's servers might be down.Update the Google App if needed."

            }
            if (error == 5) {
                message = "There was an error with your device. Restart the application.Update the Google App if needed."
            }
            if (error == 6) {
                message = "Please start talking."
            }
            if (error == 7) {
                message = "Couldn't recognize what you said. Please try again."
            }
            if (error == 9) {
                message = "Insufficient permission. Please modify the application's permissions."
            }
            saidText!!.text = message
        }

        override fun onResults(results: Bundle) {
            var str = String()
            Log.d("Info", "onResults $results")
            val data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in data!!.indices) {
                Log.d("Info", "result " + data[i])
                str += data[i]
            }
            saidText!!.text = data[0].toString()
            saidTextFlag = true
            if (saidTextFlag == true) {
                viewModel!!.recordingText = saidText!!.text.toString()
                viewModel!!.recordingFlag = true
            }
        }

        override fun onPartialResults(partialResults: Bundle) {
            Log.d("Info", "onPartialResults")
        }

        override fun onEvent(eventType: Int, params: Bundle) {
            Log.d("Info", "onEvent $eventType")
        }
    }

    public fun setTextTypingText(view: View){
        var textTyped:String?=null
        textTyped = typedTextEditText!!.text.toString()
        viewModel!!.typingTextTypedRegister = textTyped
        typedText!!.text = textTyped

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setupPermissions()
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        val binding: ActivityRegisterBinding = DataBindingUtil.setContentView<ActivityRegisterBinding>(this, com.example.myapplication.R.layout.activity_register)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel!!.registerListener = this
        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr!!.setRecognitionListener(listener())

        saidText = findViewById<TextView>(R.id.said_text_register)
        textReceivedSpeechRegister=findViewById(R.id.recording_text_register)

        typedText = findViewById(R.id.typedTextRegister)
        typedTextEditText=findViewById(R.id.editTextTypedRegister)
        textReceivedTypingRegister = findViewById(R.id.typing_text_register)
    }
}
