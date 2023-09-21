package com.test.tripfriend.ui.myinfo

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMyAppSettingBinding
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.user.LoginMainActivity
import kotlinx.coroutines.runBlocking

class MyAppSettingFragment : Fragment() {

    lateinit var fragmentMyAppSettingBinding:FragmentMyAppSettingBinding
    lateinit var mainActivity: MainActivity
    lateinit var callback: OnBackPressedCallback

    val userRepository = UserRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        fragmentMyAppSettingBinding = FragmentMyAppSettingBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val userDocumentId = arguments?.getString("userDocumentId") ?:""

        fragmentMyAppSettingBinding.run {

            appSettingToolbar.run {
                setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                setNavigationIconTint(Color.BLACK)
                //툴바 뒤로가기
                setNavigationOnClickListener {
                    mainActivity.removeFragment(MainActivity.MY_APP_SETTING_FRAGMENT)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility=View.VISIBLE
                }
            }

            //채팅 알림 스위치 변경 리스너
            switchChattingNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
                runBlocking { userRepository.updateTargetUserChatNotification(userDocumentId, isChecked) }
            }

            //푸시 알림 스위치 변경 리스너
            switchPushNotification.setOnCheckedChangeListener { compoundButton, isChecked ->
                runBlocking { userRepository.updateTargetUserPushNotification(userDocumentId, isChecked) }
            }

            //계정 정보 수정 버튼
            linearLayoutMyAppSettingModifyUserInfo.setOnClickListener {
                val newBundle = Bundle()
                newBundle.putString("userDocumentId", userDocumentId)

                mainActivity.replaceFragment(MainActivity.MODIFY_MY_INFO_FRAGMENT,true,false,newBundle)
            }

            //로그아웃 버튼
            buttonLogOut.setOnClickListener {
                val builder=MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).apply {
                    setTitle("로그아웃")
                    setMessage("현재 로그인 된 계정에서 로그아웃 됩니다")
                    setNegativeButton("취소", null)
                    setPositiveButton("로그아웃"){ dialogInterface: DialogInterface, i: Int ->
                        //로그인 된 정보 지우기
                        UserRepository.resetUserInfo(mainActivity.sharedPreferences)
                        //화면 전환
                        val intent = Intent(mainActivity, LoginMainActivity::class.java)
                        startActivity(intent)
                        mainActivity.finish()
                    }
                }
                builder.show()
            }

            //회원탈퇴 버튼
            buttonSignOut.setOnClickListener {
                MaterialAlertDialogBuilder(mainActivity,R.style.DialogTheme).run {
                    setTitle("회원탈퇴")
                    setMessage("회원탈퇴를 하시면 저장된 모든 정보가 삭제되며 삭제된 정보는 복구할 수 없습니다.")
                    setNegativeButton("취소", null)
                    setPositiveButton("회원탈퇴"){ dialogInterface: DialogInterface, i: Int ->
                        //서버에 저장된 회원 정보 삭제
                        userRepository.deleteTargetUserData(userDocumentId)
                        //로그인 된 정보 지우기
                        UserRepository.resetUserInfo(mainActivity.sharedPreferences)
                        //화면 전환
                        val intent = Intent(mainActivity, LoginMainActivity::class.java)
                        startActivity(intent)
                        mainActivity.finish()
                    }
                    show()
                }
            }
        }
        return fragmentMyAppSettingBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val main = activity as MainActivity

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                main.removeFragment(MainActivity.MY_APP_SETTING_FRAGMENT)
                main.activityMainBinding.bottomNavigationViewMain.visibility=View.VISIBLE

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}