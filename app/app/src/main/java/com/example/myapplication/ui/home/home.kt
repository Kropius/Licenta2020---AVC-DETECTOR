package com.example.myapplication.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.myapplication.ui.map.Maps
import com.example.myapplication.ui.symptoms.Symptoms
import com.example.myapplication.ui.test.normalphoto.normalPhoto


class home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        setContentView(com.example.myapplication.R.layout.activity_home)
    }
    fun startTest(view: View){
        startActivity(Intent(this,normalPhoto::class.java))
    }
    fun seeSymptoms(view:View){
        startActivity(Intent(this,Symptoms::class.java))
    }
    fun seeMap(view:View){
        startActivity(Intent(this, Maps::class.java))
    }
    override fun onBackPressed() {
        // Do Here what ever you want do on back press;
    }
}
