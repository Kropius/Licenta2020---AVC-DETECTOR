package com.example.myapplication.ui.test.typingTest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityTypingTestBinding
import com.example.myapplication.ui.test.endTest.endTest

class typingTest : AppCompatActivity(), TypingTestListener,TypingTestListenerSender {
    override fun onStartedSender() {
//        Toast.makeText(this,"Trimitem textul",Toast.LENGTH_SHORT).show()
    }

    override fun onSuccessSender(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,endTest::class.java))
    }

    override fun onFailureSender(string: String) {
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
    }

    var viewModel: TypingTestViewModel? = null
    var binding: ActivityTypingTestBinding? = null

    var textReceived: TextView? = null
    var textTyped: TextView? = null
    var myEditText: EditText? = null
    override fun onStarted() {
//        Toast.makeText(this, "Obtinem textul ce trebuie tastat!", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess(string: String, id_text: String) {
        textReceived!!.text = string
    }

    override fun onFailure(string: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        binding = DataBindingUtil.setContentView<ActivityTypingTestBinding>(this, com.example.myapplication.R.layout.activity_typing_test)
        viewModel = ViewModelProviders.of(this).get(TypingTestViewModel::class.java)
        viewModel!!.typingTestListener = this
        viewModel!!.typingTestListenerSender = this
        binding!!.viewmodel=viewModel


        textReceived = findViewById(R.id.textReceived)
        textTyped = findViewById(R.id.typedText)
        myEditText = findViewById(R.id.editTextTyped)
    }
    public fun setText(view: View){
        textTyped!!.text = myEditText!!.text
        viewModel!!.textToBeSent  = textTyped!!.text.toString()

    }
}
