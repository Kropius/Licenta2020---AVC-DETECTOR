package com.example.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

fun addTokensToPreferences(accessToken: String, refreshToken: String, context: Context) {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor: SharedPreferences.Editor = prefs.edit()
    Log.i("Info","dumnezeu"+prefs.all["token"])
    editor.putString("token", accessToken)


    editor.putString("refresh_token", refreshToken)
    editor.apply()
    Log.i("Info","dumnezeu"+prefs.all["token"])
}

fun getTokens(context: Context): String {
    val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val allEntries = mPrefs.all
    for (entry in allEntries) {
        if (entry.key.toString().equals("token")) {
            Log.i("Info","Am gasit pizda"+entry.value.toString())
            return entry.value.toString()
        }
    }
    return ""
}