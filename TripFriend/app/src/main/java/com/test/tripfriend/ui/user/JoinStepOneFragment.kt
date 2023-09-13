package com.test.tripfriend.ui.user

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentJoinStepOneBinding

class JoinStepOneFragment : Fragment() {

    lateinit var fragmentJoinStepOneBinding: FragmentJoinStepOneBinding
    lateinit var loginMainActivity: LoginMainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentJoinStepOneBinding = FragmentJoinStepOneBinding.inflate(layoutInflater)
        loginMainActivity = activity as LoginMainActivity

        fragmentJoinStepOneBinding.run {

            //툴바 설정
            materialToolbarJoinStepOne.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.JOIN_STEP_ONE_FRAGMENT)
                }
            }

            //프로그래스바 설정
            progressBarJoinStepOne.run {
                //setStateNumberTypeface("nanumbarunpenregular")
            }

            //버튼 클릭 화면 전환
            buttonJoinStepOneNext.setOnClickListener {
                loginMainActivity.replaceFragment(LoginMainActivity.JOIN_STEP_TWO_FRAGMENT, true, true, null)
            }

            //이용 약관 내용 보기
            textViewJoinStepOneServiceTerms.setOnClickListener {
                val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                    setTitle("Trip Friend 서비스 이용 약관")
                    setMessage(R.string.service_terms_contents)
                    setNegativeButton("확인", null)
                }
                builder.show()
            }

            //개인 정보 약관 내용 보기
            textViewJoinStepOnePersonalInfoTerms.setOnClickListener {
                val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                    setTitle("개인 정보 수집 및 이용 동의")
                    setMessage(R.string.personal_info_terms_contents)
                    setNegativeButton("확인", null)
                }
                builder.show()
            }
        }

        return fragmentJoinStepOneBinding.root

    }
}