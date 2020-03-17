package com.example.myapplication.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.example.myapplication.R

class home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        setContentView(R.layout.activity_home)
    }
}
