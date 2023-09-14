package com.test.tripfriend.ui.trip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentModifyPostBinding

class ModifyPostFragment : Fragment() {
    lateinit var fragmentModifyPostBinding: FragmentModifyPostBinding
    lateinit var mainActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentModifyPostBinding = FragmentModifyPostBinding.inflate(layoutInflater)

        fragmentModifyPostBinding.run {
            materialToolbarModifyPost.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MODFY_POST_FRAGMENT)
                }
            }
            mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
            buttonModifyPostToNextView.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MODFY_POST2_FRAGMENT,true,true,null)
            }
        }

        return fragmentModifyPostBinding.root
    }

    override fun onPause() {
        super.onPause()
        mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.VISIBLE
    }
}
