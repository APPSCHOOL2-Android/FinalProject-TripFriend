package com.test.tripfriend.ui.user

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentJoinStepThreeBinding
import kotlin.math.log

class JoinStepThreeFragment : Fragment() {

    lateinit var fragmentJoinStepThreeBinding: FragmentJoinStepThreeBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentJoinStepThreeBinding = FragmentJoinStepThreeBinding.inflate(inflater)
        loginMainActivity = activity as LoginMainActivity

        fragmentJoinStepThreeBinding.run {
            materialToolbarJoinStepThree.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT)
                }
            }

            progressBarJoinStepThree.run {
                setOnStateItemClickListener { stateProgressBar, stateItem, stateNumber, isCurrentState ->
                    when(stateNumber){
                        1-> {
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT)
                        }
                        2-> { loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT) }
                    }
                }
            }

            buttonJoinStepThreeNext.setOnClickListener {
                loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT, true, true, null)
            }
        }

        return fragmentJoinStepThreeBinding.root
    }
}