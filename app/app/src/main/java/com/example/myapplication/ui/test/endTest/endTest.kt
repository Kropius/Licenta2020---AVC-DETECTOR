package com.example.myapplication.ui.test.endTest

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityEndTestBinding
import com.example.myapplication.ui.home.home
import kotlinx.android.synthetic.main.activity_end_test.view.*
import org.w3c.dom.Text

class endTest : AppCompatActivity(), endTesteListener {
    override fun onStarted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSuccess(verdictTitle: String?, verdictSubTytle: String?, verdictTips: String?, vbuttonEndText: String) {
        this.verdictTitle!!.text = verdictTitle
        this.verdictSubTytle!!.text = verdictSubTytle
        this.verdictTips!!.text = verdictTips
        this.vbuttonEndText!!.text = vbuttonEndText
        if(this.verdictTitle!!.text != "You are okay!"){
            this.buttonCall!!.setOnClickListener { call911() }
        }
        else{
            this.buttonCall!!.setOnClickListener { callFriend() }

        }
    }

    override fun onFailure() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var viewModel: EndTestViewModel? = null
    var binding: ActivityEndTestBinding? = null
    var verdictTitle: TextView? = null
    var verdictSubTytle: TextView? = null
    var verdictTips: TextView? = null
    var vbuttonEndText: Button? = null
    var buttonCall:Button?=null
    public fun call911() {
        startActivity(Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:911") })
    }
    public fun callFriend(){
        startActivity(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide();

        binding = DataBindingUtil.setContentView<ActivityEndTestBinding>(this, com.example.myapplication.R.layout.activity_end_test)
        viewModel = ViewModelProviders.of(this).get(EndTestViewModel::class.java)
        binding!!.viewmodel = viewModel
        verdictTitle = findViewById(R.id.verdict)
        verdictSubTytle = findViewById(R.id.verdict_subtitle)
        verdictTips = findViewById(R.id.tips)
        vbuttonEndText = findViewById(R.id.verdict_call)
        buttonCall = findViewById(R.id.verdict_call)
        viewModel!!.endTestListener = this
        viewModel!!.printResults()
    }

    public fun goHome(view: View) {
        startActivity(Intent(this, home::class.java))
    }
}
