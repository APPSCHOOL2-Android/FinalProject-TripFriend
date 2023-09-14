package com.test.tripfriend.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.databinding.FragmentHomeMapBinding

class HomeMapFragment : Fragment() {
    lateinit var fragmentHomeMapBinding: FragmentHomeMapBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHomeMapBinding = FragmentHomeMapBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return fragmentHomeMapBinding.root


    }
}