package com.test.tripfriend.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentModifyPost3Binding

class ModifyPost3Fragment : Fragment() {
    lateinit var fragmentModifyPost3Binding: FragmentModifyPost3Binding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentModifyPost3Binding = FragmentModifyPost3Binding.inflate(layoutInflater)

        fragmentModifyPost3Binding.run {
            materialToolbarModifyPost3.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODFY_POST3_FRAGMENT)
                }
            }
            mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
            buttonAccompanyModifyPost3ToSubmit.setOnClickListener {
                mainActivity.removeFragment(MainActivity.MODFY_POST3_FRAGMENT)
                mainActivity.removeFragment(MainActivity.MODFY_POST2_FRAGMENT)
                mainActivity.removeFragment(MainActivity.MODFY_POST_FRAGMENT)
            }
        }

        return fragmentModifyPost3Binding.root
    }

    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}