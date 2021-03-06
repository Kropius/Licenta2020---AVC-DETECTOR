package com.example.myapplication.ui.firstscreen

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.Bundle
import android.view.View

import com.example.myapplication.R
import com.example.myapplication.ui.auth.Login
import com.example.myapplication.ui.auth.Register

class FirstScreen : AppCompatActivity() {

    fun login(view: View) {
        startActivity(Intent(this, Login::class.java))
    }
    fun register(view: View) {
        startActivity(Intent(this, Register::class.java))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        setContentView(R.layout.activity_first)
    }
}
