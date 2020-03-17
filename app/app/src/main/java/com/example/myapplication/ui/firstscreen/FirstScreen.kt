package com.example.myapplication.ui.firstscreen

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.example.myapplication.R
import com.example.myapplication.ui.auth.Login

class FirstScreen : AppCompatActivity() {

    fun login(view: View) {
        startActivity(Intent(this, Login::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        setContentView(R.layout.activity_first)
    }
}
