package com.example.myapplication.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.util.hide
import com.example.myapplication.util.show
import com.example.myapplication.util.toast
import kotlinx.android.synthetic.main.activity_login.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import com.example.myapplication.ui.firstscreen.FirstScreen
import com.example.myapplication.ui.home.home
import dagger.Component
import kotlinx.coroutines.awaitAll


@Component
interface ApplicationComponent {}

class Login : AppCompatActivity(), LoginListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, com.example.myapplication.R.layout.activity_login)
        val viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel
        viewModel.loginListener = this

    }

    override fun onStared() {
        progress_bar.show()
    }

    override fun onSuccess(response: String?) {
        progress_bar.hide()
        toast(response!!)

        if (!(response.toString().equals("Incorrect data!"))) {
            startActivity(Intent(this, home::class.java))
        }

    }

    override fun onFailure(message: String) {
        progress_bar.hide()
        toast(message)
    }

}