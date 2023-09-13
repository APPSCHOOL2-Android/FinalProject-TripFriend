package com.test.tripfriend.ui.user

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentJoinStepTwoBinding


class JoinStepTwoFragment : Fragment() {

    lateinit var fragmentJoinStepTwoBinding: FragmentJoinStepTwoBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentJoinStepTwoBinding = FragmentJoinStepTwoBinding.inflate(inflater)
        loginMainActivity = activity as LoginMainActivity

        fragmentJoinStepTwoBinding.run {
            //툴바 백버튼 설정
            materialToolbarJoinStepTwo.run{
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT)
                }
            }

            //프로그래스 바 설정
            progressBarJoinStepTwo.run {
                setOnStateItemClickListener { stateProgressBar, stateItem, stateNumber, isCurrentState ->
                    when(stateNumber){
                        1-> { loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT) }
                    }
                }
            }

            //버튼 클릭 화면 전환
            buttonJoinStepTwoNext.setOnClickListener {
                loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT, true, true, null)
            }
        }

        return fragmentJoinStepTwoBinding.root
    }

}