package com.test.tripfriend.ui.accompany

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.MainActivity
import com.test.tripfriend.databinding.FragmentAccompanyRegister3Binding
import com.test.tripfriend.ui.user.LoginMainActivity

class AccompanyRegisterFragment3 : Fragment() {
    lateinit var fragmentAccompanyRegisterFragment3: FragmentAccompanyRegister3Binding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAccompanyRegisterFragment3 = FragmentAccompanyRegister3Binding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentAccompanyRegisterFragment3.run {
            materialToolbarRegister3.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
                }
            }

            buttonAccompanyRegister3ToSubmit.setOnClickListener {
                Snackbar.make(mainActivity.activityMainBinding.root, "등록이 완료되었습니다..", Snackbar.LENGTH_SHORT).show()

                // 내가 쓴 글창으로 가야하는데 파일이 없어서 일단 메인 홈으로 이동시켰습니다.
                mainActivity.replaceFragment(MainActivity.HOME_MAIN_FRAGMENT, false, true, null)
                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT3)
                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT2)
                mainActivity.removeFragment(MainActivity.ACCOMPANY_REGISTER_FRAGMENT1)
            }
        }

        return fragmentAccompanyRegisterFragment3.root
    }
}