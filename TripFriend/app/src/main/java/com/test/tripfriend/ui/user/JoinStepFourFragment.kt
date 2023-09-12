package com.test.tripfriend.ui.user

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentJoinStepFourBinding

class JoinStepFourFragment : Fragment() {

    lateinit var fragmentJoinStepFourBinding: FragmentJoinStepFourBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentJoinStepFourBinding = FragmentJoinStepFourBinding.inflate(inflater)
        loginMainActivity = activity as LoginMainActivity

        fragmentJoinStepFourBinding.run {
            materialToolbarJoinStepFour.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT)
                }
            }

            progressBarJoinStepFour.run {
                setOnStateItemClickListener { stateProgressBar, stateItem, stateNumber, isCurrentState ->
                    when(stateNumber){
                        1-> {}
                        2-> {}
                        3-> { loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT) }
                        4-> {}
                        5-> { loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT, true, true, null) }
                    }
                }
            }

            buttonJoinStepFourNext.setOnClickListener {
                loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT, true, true, null)
            }
        }

        return fragmentJoinStepFourBinding.root
    }
}