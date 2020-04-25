package com.example.chirag.slidingtabsusingviewpager

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.ui.symptoms.ArmWeakness
import com.example.myapplication.ui.symptoms.FragmentFace
import com.example.myapplication.ui.symptoms.FragmentSpeech
import com.example.myapplication.util.PageAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_symptoms.*


class Symptoms : AppCompatActivity(), FragmentFace.OnFragmentInteractionListener, FragmentSpeech.OnFragmentInteractionListener,ArmWeakness.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.myapplication.R.layout.activity_symptoms)
        getSupportActionBar()?.hide();

        val viewPager = findViewById(com.example.myapplication.R.id.pager) as ViewPager
        val adapter: PageAdapter = PageAdapter(supportFragmentManager)
        val tabLayout = findViewById(com.example.myapplication.R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(pager, true)
        viewPager.adapter = adapter
    }

}