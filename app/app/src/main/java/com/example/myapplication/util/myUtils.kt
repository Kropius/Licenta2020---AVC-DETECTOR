package com.example.myapplication.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

fun addTokensToPreferences(accessToken: String, refreshToken: String, context: Context) {
    val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val editor: SharedPreferences.Editor = prefs.edit()
    editor.putString("token", accessToken)
    editor.putString("refresh_token", refreshToken)
    editor.apply()
}

fun getTokens(context: Context) {
    val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val allEntries = mPrefs.all
    for (entry in allEntries) {
        Log.i("Info", entry.key + ": " +
                entry.value.toString())
    }
}