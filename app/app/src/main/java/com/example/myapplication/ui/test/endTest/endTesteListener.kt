package com.example.myapplication.ui.test.endTest

interface endTesteListener {
    fun onStarted();
    fun onSuccess( verdictTitle:String?,
                   verdictSubTytle:String?,
                    verdictTips:String?,
                   vbuttonEndText:String);
    fun onFailure();
}