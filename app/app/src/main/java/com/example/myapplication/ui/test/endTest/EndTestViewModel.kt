package com.example.myapplication.ui.test.endTest

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.R
import com.example.myapplication.data.network.responses.endTestResponse
import com.example.myapplication.data.repositories.EndTestRepository
import com.example.myapplication.util.Coroutines
import com.example.myapplication.util.getTokens
import retrofit2.Response

class EndTestViewModel(application:Application):AndroidViewModel(application) {
    var context:Application = getApplication()
    var verdictTitle:String? = null
    var verdictSubTytle:String? = null
    var verdictTips:String? = null
    var buttonEndText:String?=null

    var endTestListener:endTesteListener? = null
    public fun printResults(){
        Coroutines.main{
            val response: Response<endTestResponse> = EndTestRepository().endTestRepository(getTokens(getApplication()),getApplication())
            if(response.isSuccessful){
                if(response.body()!!.response == "You are ok! Calm down"){
                    verdictTitle = context.resources.getString(R.string.end_test_good)
                    verdictSubTytle = context.resources.getString(R.string.end_test_verdict_good)
                    verdictTips = context.resources.getString(R.string.advice_good)
                    buttonEndText = context.resources.getString(R.string.call_a_friend )
                    endTestListener!!.onSuccess(verdictTitle!!,verdictSubTytle!!,verdictTips!!,buttonEndText!!)
                }
                else{
                    verdictTitle = context.resources.getString(R.string.end_test_bad)
                    verdictSubTytle = context.resources.getString(R.string.end_test_verdict_bad)
                    verdictTips = context.resources.getString(R.string.advice_bad)
                    buttonEndText = context.resources.getString(R.string.call911 )
                    endTestListener!!.onSuccess(verdictTitle,verdictSubTytle,verdictTips,buttonEndText!!)

                }

            }
            else{
                Toast.makeText(context,"matancurs",Toast.LENGTH_LONG).show()

            }
        }

    }
}