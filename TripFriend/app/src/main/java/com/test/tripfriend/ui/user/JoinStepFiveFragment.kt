package com.test.tripfriend.ui.user

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.get
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentJoinStepFiveBinding


class JoinStepFiveFragment : Fragment() {

    lateinit var fragmentJoinStepFiveBinding: FragmentJoinStepFiveBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentJoinStepFiveBinding = FragmentJoinStepFiveBinding.inflate(inflater)
        loginMainActivity = activity as LoginMainActivity

        fragmentJoinStepFiveBinding.run {
            materialToolbarJoinStepFive.run{
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT)
                }
            }

            progressBarJoinStepFive.run {
                setOnStateItemClickListener { stateProgressBar, stateItem, stateNumber, isCurrentState ->
                    when(stateNumber){
                        1-> {
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT)
                        }
                        2-> {
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT)
                        }
                        3-> {
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT)
                            loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT)
                        }
                        4-> { loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT) }
                    }
                }
            }

            buttonJoinStepFiveSubmit.setOnClickListener {

                //가입 완료 시 회원가입 단계 화면 백스택에서 전부 제거
                loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FIVE_FRAGMENT)
                loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_FOUR_FRAGMENT)
                loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_THREE_FRAGMENT)
                loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT)
                loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_ONE_FRAGMENT)

                Snackbar.make(fragmentJoinStepFiveBinding.root, "회원가입이 완료되었습니다.", Snackbar.LENGTH_SHORT).show()
            }

            chipJoinStepFiveNon.setOnCheckedChangeListener { compoundButton, checked ->
                //모름 칩 선택되었을 때 다이얼로그 발생
                if(checked){
                    val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                        setTitle("MBTI 등록")
                        setMessage("MBTI를 등록하면 다른 사용자로부터 더 많은 동행 신청을 받을 수 있습니다.")
                        setNegativeButton("없음으로 등록", null)
                        setPositiveButton("MBTI 등록"){ dialogInterface: DialogInterface, i: Int ->
                            chipJoinStepFiveNon.isChecked = false
                        }
                    }
                    builder.show()
                }
            }
        }

        return fragmentJoinStepFiveBinding.root
    }
}