package com.test.tripfriend

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.test.tripfriend.databinding.ActivityMainBinding
import com.test.tripfriend.ui.accompany.AccompanyRegisterFragment1
import com.test.tripfriend.ui.accompany.AccompanyRegisterFragment2
import com.test.tripfriend.ui.accompany.AccompanyRegisterFragment3
import com.test.tripfriend.ui.chatting.ChattingMainFragment
import com.test.tripfriend.ui.home.HomeListFragment
import com.test.tripfriend.ui.chatting.GroupChatRoomFragment
import com.test.tripfriend.ui.chatting.PersonalChatRoomFragment
import com.test.tripfriend.ui.home.HomeMainFragment
import com.test.tripfriend.ui.home.HomeMapFragment
import com.test.tripfriend.ui.myinfo.ModifyMyInfoFragment
import com.test.tripfriend.ui.myinfo.MyAccompanyInfoFragment
import com.test.tripfriend.ui.myinfo.MyAppSettingFragment
import com.test.tripfriend.ui.myinfo.MyInfoMainFragment
import com.test.tripfriend.ui.trip.ModifyPostFragment
import com.test.tripfriend.ui.trip.NotificationFragment
import com.test.tripfriend.ui.trip.ReadPostFragment
import com.test.tripfriend.ui.trip.ReviewFragment
import com.test.tripfriend.ui.trip.TripMainFragment
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    var selectedTabPosition:Int = 0

    lateinit var activityMainBinding: ActivityMainBinding

    // 키보드 관리자
    lateinit var inputMethodManager: InputMethodManager

    var selectMenu = 0

    companion object {
        val HOME_MAIN_FRAGMENT = "HomeMainFragment"
        val HOME_LIST_FRAGMENT = "HomeListFragment"
        val HOME_MAP_FRAGMENT = "HomeMapFragment"
        val TRIP_MAIN_FRAGMENT = "TripMainFragment"
        val CHATTING_MAIN_FRAGMENT = "ChattingMainFragment"
        val MYINFO_MAIN_FRAGMENT = "MyInfoMainFragment"
        val NOTIFICATION_FRAGMENT = "NotificationFragment"
        val MY_INFO_MAIN_FRAGMENT = "MyInfoMainFragment"
        val MY_ACCOMPANY_INFO_FRAGMENT="MyAccompanyInfoFragment"
        val MY_APP_SETTING_FRAGMENT="MyAppSettingFragment"
        val MODIFY_MY_INFO_FRAGMENT="ModifyMyInfoFragment"
        val ACCOMPANY_REGISTER_FRAGMENT1 = "AccompanyRegisterFragment1"
        val ACCOMPANY_REGISTER_FRAGMENT2 = "AccompanyRegisterFragment2"
        val ACCOMPANY_REGISTER_FRAGMENT3 = "AccompanyRegisterFragment3"
        val PERSONAL_CHAT_ROOM_FRAGMENT = "PersonalChatRoomFragment"
        val GROUP_CHAT_ROOM_FRAGMENT = "GroupChatRoomFragment"
        val READ_POST_FRAGMENT = "ReadPostFragment"
        val MODFY_POST_FRAGMENT = "ModifyPostFragment"
        val REVIEW_FRAGMENT = "ReviewFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        // 시작화면 설정
        replaceFragment(HOME_MAIN_FRAGMENT, false, true, null)

        activityMainBinding.run {
            bottomNavigationViewMain.run {
                this.selectedItemId = R.id.navigationHome
                selectMenu = R.id.navigationHome

                setOnItemSelectedListener {
                    //선택된 메뉴를 다시 클릭할 때 선택을 넘기는 조건문
                    if (it.itemId == selectedItemId){
                        return@setOnItemSelectedListener false
                    }
                    when (it.itemId) {
                        //홈 클릭
                        R.id.navigationHome -> {
                            selectMenu = it.itemId
                            replaceFragment(HOME_MAIN_FRAGMENT, false, true, null)
                        }
                        //여행 클릭
                        R.id.navigationTrip -> {
                            selectMenu = it.itemId
                            replaceFragment(TRIP_MAIN_FRAGMENT,false,true,null)
                        }
                        //채팅 클릭
                        R.id.navigationChatting -> {
                            selectMenu = it.itemId
                            replaceFragment(CHATTING_MAIN_FRAGMENT, false, true, null)
                        }
                        //내정보 클릭
                        R.id.navigationMyInfo -> {
                            selectMenu = it.itemId
                            replaceFragment(MY_INFO_MAIN_FRAGMENT, false, true, null)
                        }
                        else -> {
                            replaceFragment(HOME_MAIN_FRAGMENT, false, true, null)
                        }
                    }
                    true
                }
            }
        }
    }

    // 지정한 Fragment를 보여주는 메서드
    fun replaceFragment(name: String, addToBackStack: Boolean, animate: Boolean, bundle: Bundle?) {
        // Fragment 교체 상태로 설정한다.
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // 새로운 Fragment를 담을 변수
        var newFragment = when (name) {
            HOME_MAIN_FRAGMENT -> HomeMainFragment()
            HOME_LIST_FRAGMENT -> HomeListFragment()
            HOME_MAP_FRAGMENT -> HomeMapFragment()
            TRIP_MAIN_FRAGMENT -> TripMainFragment()
            CHATTING_MAIN_FRAGMENT -> ChattingMainFragment()
            MYINFO_MAIN_FRAGMENT -> MyInfoMainFragment()
            NOTIFICATION_FRAGMENT -> NotificationFragment()
            MY_INFO_MAIN_FRAGMENT -> MyInfoMainFragment()
            MY_ACCOMPANY_INFO_FRAGMENT->MyAccompanyInfoFragment()
            ACCOMPANY_REGISTER_FRAGMENT1 -> AccompanyRegisterFragment1()
            ACCOMPANY_REGISTER_FRAGMENT2 -> AccompanyRegisterFragment2()
            ACCOMPANY_REGISTER_FRAGMENT3 -> AccompanyRegisterFragment3()
            MY_APP_SETTING_FRAGMENT->MyAppSettingFragment()
            MODIFY_MY_INFO_FRAGMENT->ModifyMyInfoFragment()
            MYINFO_MAIN_FRAGMENT -> MyInfoMainFragment()
            PERSONAL_CHAT_ROOM_FRAGMENT -> PersonalChatRoomFragment()
            GROUP_CHAT_ROOM_FRAGMENT -> GroupChatRoomFragment()
            READ_POST_FRAGMENT -> ReadPostFragment()
            MODFY_POST_FRAGMENT ->ModifyPostFragment()
            REVIEW_FRAGMENT -> ReviewFragment()

            else -> Fragment()
        }

        newFragment.arguments = bundle

        if (newFragment != null) {
            // Fragment를 교체한다.
            fragmentTransaction.replace(R.id.fragmentContainerMain, newFragment)

            if (animate) {
                // 애니메이션을 설정한다.
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            }

            if (addToBackStack) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    // Fragment를 BackStack에서 제거한다.
    fun removeFragment(name: String) {
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // 키보드를 올려주는 메서드
    fun showSoftInput(view: View, delay:Long){
        view.requestFocus()
        thread {
            SystemClock.sleep(delay)
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    // 키보드를 내려주는 메서드
    fun hideSoftInput(){
        if(currentFocus != null){

            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}