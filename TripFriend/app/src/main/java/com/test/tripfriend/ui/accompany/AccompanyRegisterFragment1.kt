package com.test.tripfriend.ui.accompany

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.ui.main.MainActivity
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

        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE

        fragmentAccompanyRegisterFragment1.run {
            materialToolbarRegister1.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1)
                }
            }

            buttonAccompanyRegister1ToNextView.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2, true, true, null)
            }
        }

        return fragmentAccompanyRegisterFragment1.root
    }
}