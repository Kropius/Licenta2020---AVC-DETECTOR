package com.example.myapplication.ui.auth

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repositories.UserRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.addTokensToPreferences
import com.example.myapplication.util.getTokens


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val context: Context = getApplication()
    var username: String? = null
    var password: String? = null
    var loginListener: LoginListener? = null

    fun onLoginButtonClick(view: View) {
        loginListener!!.onStared()
        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            loginListener!!.onFailure("Invalid Email or password")
            return
        }
        Coroutines.main {
            val loginRespone = UserRepository().userLogin(username!!, password!!, context)
            if (loginRespone.isSuccessful) {
                loginListener?.onSuccess(loginRespone.body()?.succes)
                if (loginRespone.body()?.succes.toString().equals("Correct data!")) {
                    val login = loginRespone.body()
                    addTokensToPreferences(login?.access_token.toString(), login?.refresh_token.toString(), getApplication())
                    getTokens(getApplication())
                }
            } else {

                loginListener?.onFailure("Error!")
            }
        }
    }
}