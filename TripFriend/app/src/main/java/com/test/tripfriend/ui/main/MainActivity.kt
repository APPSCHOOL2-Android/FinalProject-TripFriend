package com.test.tripfriend.ui.main

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.libraries.places.api.Places
import com.test.tripfriend.BuildConfig
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.R
import com.test.tripfriend.databinding.ActivityMainBinding
import com.test.tripfriend.dataclassmodel.UserLogin
import com.test.tripfriend.repository.UserRepository
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
import com.test.tripfriend.ui.trip.InProgressFragment
import com.test.tripfriend.ui.trip.ModifyPost2Fragment
import com.test.tripfriend.ui.trip.ModifyPost3Fragment
import com.test.tripfriend.ui.trip.ModifyPostFragment
import com.test.tripfriend.ui.trip.NotificationFragment
import com.test.tripfriend.ui.trip.ReadPostFragment
import com.test.tripfriend.ui.trip.ReviewFragment
import com.test.tripfriend.ui.trip.TripMainFragment
import com.test.tripfriend.ui.user.LoginMainFragment
import com.test.tripfriend.ui.user.LoginMainActivity
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding

    lateinit var sharedPreferences: SharedPreferences
    lateinit var userClass : UserLogin

    var tripMainPosition = 0
    var homeMainPosition = 0

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
        val MODFY_POST2_FRAGMENT = "ModifyPost2Fragment"
        val MODFY_POST3_FRAGMENT = "ModifyPost3Fragment"
        val REVIEW_FRAGMENT = "ReviewFragment"
        val INPROGRESS_FRAGMENT = "InProgressFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        userClass = UserRepository.getUserInfo(sharedPreferences)

        val apiKey = BuildConfig.MAPS_API_KEY
        if (apiKey.isEmpty()) {
            Toast.makeText(this, "No API key defined in gradle.properties", Toast.LENGTH_LONG).show()
            return
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

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
                            checkLoginUserIsMember(userClass.userName,
                                callback1 = {
                                    bottomNavigationViewMain.selectedItemId = R.id.navigationHome
                                    selectMenu = R.id.navigationHome
                                },
                                callback2 = {
                                    replaceFragment(TRIP_MAIN_FRAGMENT,false,true,null)
                                    selectMenu = it.itemId
                                }
                            )
                        }
                        //채팅 클릭
                        R.id.navigationChatting -> {
                            checkLoginUserIsMember(userClass.userName,
                                callback1 = {
                                    bottomNavigationViewMain.selectedItemId = R.id.navigationHome
                                    selectMenu = R.id.navigationHome
                                },
                                callback2 = {
                                    selectMenu = it.itemId
                                    replaceFragment(CHATTING_MAIN_FRAGMENT, false, true, null)
                                }
                            )
                        }
                        //내정보 클릭
                        R.id.navigationMyInfo -> {
                            checkLoginUserIsMember(userClass.userName,
                                callback1 = {
                                    bottomNavigationViewMain.selectedItemId = R.id.navigationHome
                                    selectMenu = R.id.navigationHome
                                },
                                callback2 = {
                                    selectMenu = it.itemId
                                    replaceFragment(MY_INFO_MAIN_FRAGMENT, false, true, null)
                                }
                            )
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
            MY_ACCOMPANY_INFO_FRAGMENT ->MyAccompanyInfoFragment()
            ACCOMPANY_REGISTER_FRAGMENT1 -> AccompanyRegisterFragment1()
            ACCOMPANY_REGISTER_FRAGMENT2 -> AccompanyRegisterFragment2()
            ACCOMPANY_REGISTER_FRAGMENT3 -> AccompanyRegisterFragment3()
            MY_APP_SETTING_FRAGMENT ->MyAppSettingFragment()
            MODIFY_MY_INFO_FRAGMENT ->ModifyMyInfoFragment()
            MYINFO_MAIN_FRAGMENT -> MyInfoMainFragment()
            PERSONAL_CHAT_ROOM_FRAGMENT -> PersonalChatRoomFragment()
            GROUP_CHAT_ROOM_FRAGMENT -> GroupChatRoomFragment()
            READ_POST_FRAGMENT -> ReadPostFragment()
            MODFY_POST_FRAGMENT ->ModifyPostFragment()
            MODFY_POST2_FRAGMENT -> ModifyPost2Fragment()
            MODFY_POST3_FRAGMENT -> ModifyPost3Fragment()
            REVIEW_FRAGMENT -> ReviewFragment()
            INPROGRESS_FRAGMENT -> InProgressFragment()

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

    //uri를 이미지뷰에 셋팅하는 함수
    fun setImage(image: Uri, imageView: ImageView){
        val inputStream = contentResolver.openInputStream(image)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        //회전 각도값을 가져옴
        val degree = getDegree(image)

        //회전 이미지를 생성한다
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, false)

        //글라이드 라이브러리로 view에 이미지 출력
        Glide.with(this).load(rotateBitmap)
            .into(imageView)
    }

    // 이미지 파일에 기록되어 있는 회전 정보를 가져온다.
    fun getDegree(uri: Uri) : Int{
        var exifInterface: ExifInterface? = null

        // 사진 파일로 부터 tag 정보를 관리하는 객체를 추출한다.
        try {
            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                exifInterface = ExifInterface(inputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var degree = 0
        if(exifInterface != null){
            // 각도 값을 가지고온다.
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)

            when(orientation){
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        }
        return degree
    }

    fun checkLoginUserIsMember(memberName : String, callback1 : () -> Unit, callback2: () -> Unit){
        if(memberName == "NoneUserName"){
            createDialog("로그인", "로그인이 필요한 서비스입니다."){
                callback1()
            }
        }else{
            callback2()
        }
    }

    //다이얼로그 생성 함수
    fun createDialog(title : String, message : String, callback : () -> Unit){
        MaterialAlertDialogBuilder(this@MainActivity ,R.style.DialogTheme).run {
            setTitle(title)
            setMessage(message)
            setPositiveButton("로그인하기"){ dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(this@MainActivity, LoginMainActivity::class.java)
                startActivity(intent)
                finish()
            }
            setNegativeButton("취소"){ dialogInterface: DialogInterface, i: Int ->
                callback()
            }
            show()
        }
    }
}