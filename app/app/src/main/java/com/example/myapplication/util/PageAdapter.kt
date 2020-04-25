package com.example.myapplication.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.myapplication.ui.symptoms.ArmWeakness
import com.example.myapplication.ui.symptoms.FragmentFace
import com.example.myapplication.ui.symptoms.FragmentSpeech

class PageAdapter(fm:FragmentManager): FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        if(position == 0){
            return FragmentFace()}
        else if(position ==1){
                return ArmWeakness()
            }
        else if(position == 2){
            return FragmentSpeech();
        }
        return FragmentFace();
    }

    override fun getCount(): Int {
        return 3;
    }
}