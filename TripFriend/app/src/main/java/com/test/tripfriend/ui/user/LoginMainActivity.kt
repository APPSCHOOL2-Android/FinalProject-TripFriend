package com.test.tripfriend.ui.user

import android.Manifest
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.test.tripfriend.R
import com.test.tripfriend.databinding.ActivityLoginMainBinding


class LoginMainActivity : AppCompatActivity() {

    var userAuth = ""
    var userEmail = ""
    var userPw = ""
    var userName = ""
    var userNickname = ""
    var userPhoneNumber = ""
    var userMBTI = ""
    var checkEmail = 0

    lateinit var activityLoginMainBinding: ActivityLoginMainBinding

    companion object {
        val LOGIN_MAIN_FRAGMENT = "LoginMainFragment"
        val EMAIL_LOGIN_FRAGMENT = "EmailLoginFragment"
        val JOIN_STEP_ONE_FRAGMENT = "JoinStepOneFragment"
        val JOIN_STEP_TWO_FRAGMENT = "JoinStepTwoFragment"
        val JOIN_STEP_THREE_FRAGMENT = "JoinStepThreeFragment"
        val JOIN_STEP_FOUR_FRAGMENT = "JoinStepFourFragment"
        val JOIN_STEP_FIVE_FRAGMENT = "JoinStepFiveFragment"
        val MORE_KAKAO_INFO_INPUT_FRAGMENT = "MoreKakaoInfoInputFragment"
        val MORE_NAVER_INFO_INPUT_FRAGMENT = "MoreNaverInfoInputFragment"
    }

    val permissionList = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        requestPermissions(permissionList, 0)

        activityLoginMainBinding = ActivityLoginMainBinding.inflate(layoutInflater)
        activityLoginMainBinding.run {

        }

        setContentView(activityLoginMainBinding.root)
        replaceFragment(LOGIN_MAIN_FRAGMENT, false, true, null)
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name:String, addToBackStack:Boolean, animate:Boolean, bundle:Bundle?){
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // 새로운 Fragment를 담을 변수
        var newFragment = when(name){
            LOGIN_MAIN_FRAGMENT -> LoginMainFragment()
            EMAIL_LOGIN_FRAGMENT -> EmailLoginFragment()
            JOIN_STEP_ONE_FRAGMENT -> JoinStepOneFragment()
            JOIN_STEP_TWO_FRAGMENT -> JoinStepTwoFragment()
            JOIN_STEP_THREE_FRAGMENT -> JoinStepThreeFragment()
            JOIN_STEP_FOUR_FRAGMENT -> JoinStepFourFragment()
            JOIN_STEP_FIVE_FRAGMENT -> JoinStepFiveFragment()
            MORE_KAKAO_INFO_INPUT_FRAGMENT -> MoreKakaoInfoInputFragment()
            MORE_NAVER_INFO_INPUT_FRAGMENT -> MoreNaverInfoInputFragment()
            else -> Fragment()
        }

        newFragment.arguments = bundle

        if(newFragment != null) {
            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.fragmentContainerViewLoginMain, newFragment)

            if (animate == true) {
                // 애니메이션을 설정한다.
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name:String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}