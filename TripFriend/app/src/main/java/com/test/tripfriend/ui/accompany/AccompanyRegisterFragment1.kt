package com.test.tripfriend.ui.accompany

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister1Binding

class AccompanyRegisterFragment1 : Fragment() {
    lateinit var fragmentAccompanyRegisterFragment1: FragmentAccompanyRegister1Binding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegisterFragment1 = FragmentAccompanyRegister1Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentAccompanyRegisterFragment1.run {
            buttonAccompanyRegister1ToNextView.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2, true, true, null)
            }
        }

        return fragmentAccompanyRegisterFragment1.root
    }
}