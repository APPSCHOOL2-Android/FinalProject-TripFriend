package com.test.tripfriend.ui.myinfo

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.test.tripfriend.ui.main.MainActivity
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMyInfoMainBinding
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking

class MyInfoMainFragment : Fragment() {

    lateinit var fragmentMyInfoMainBinding : FragmentMyInfoMainBinding
    lateinit var mainActivity : MainActivity

    lateinit var userViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        fragmentMyInfoMainBinding = FragmentMyInfoMainBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        fragmentMyInfoMainBinding.run {
            myInfoToolbar.run {
                setNavigationIconTint(Color.BLACK)

                setOnMenuItemClickListener {
                    //톱니 바퀴 클릭시 앱 설정 창으로 이동
                    val newBundle = Bundle()
                    newBundle.putString("userDocumentId", userViewModel.userDocumentId.value.toString())

                    mainActivity.replaceFragment(MainActivity.MY_APP_SETTING_FRAGMENT,true,false,newBundle)
                    mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
                    true
                }
            }

            //자세히 보기 클릭 시
            buttonToDetailFriendSpeed.setOnClickListener {
                mainActivity.replaceFragment(MainActivity.MY_ACCOMPANY_INFO_FRAGMENT,true,false,null)
                mainActivity.activityMainBinding.bottomNavigationViewMain.visibility = View.GONE
            }

            seekbarFriendSpeed.isEnabled = false

        }

        initViewModel(mainActivity.userClass.userEmail)

        return fragmentMyInfoMainBinding.root
    }

    fun initViewModel(userEmail : String){
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.getTargetUserData(userEmail)

        userViewModel.user.observe(viewLifecycleOwner){ user ->
            if(user != null){
                fragmentMyInfoMainBinding.run {
                    if(user.userProfilePath.isEmpty()){
                        //기본 이미지 지정
                        imageViewMyInfoMainProfile.setImageResource(R.drawable.person_24px)
                    }
                    else{
                        //유저 정보에 저장된 이미지 지정
                        userViewModel.getTargetUserProfileImage(user.userProfilePath)
                    }

                    //인증 수단
                    when(user.userAuthentication){
                        "이메일" -> imageViewAppAuth.visibility = View.VISIBLE
                        "카카오" -> imageViewKakaoAuth.visibility = View.VISIBLE
                        "네이버" -> imageViewNaverAuth.visibility = View.VISIBLE
                    }

                    //닉네임, MBTI
                    textViewMyNickname.text = user.userNickname
                    textViewMBTI.text = if(user.userMBTI == "모름") "MBTI 미설정" else user.userMBTI

                    //속도, 점수, 횟수
                    var speed = user.userFriendSpeed * 10
                    textViewFriendSpeed.text = "${speed/10}km"
                    seekbarFriendSpeed.progress = speed.toInt()
                    textViewAccompanyScore.text = user.userTripScore.toString()
                    textViewAccompanyNumber.text = user.userTripCount.toString()

                    //텍스트 뷰 위치 지정
                    var speedPerTen = speed / 10
                    val moveXTemp = (speedPerTen - 10)
                    val moveX = moveXTemp +(moveXTemp/8)

                    val dpValue = 53+(moveX*3)
                    val density = resources.displayMetrics.density
                    val pixelValue = (dpValue * density).toInt()

                    val layoutParams = textViewFriendSpeed.layoutParams as ViewGroup.MarginLayoutParams
                    // 왼쪽 마진 설정
                    layoutParams.leftMargin = pixelValue
                    // 변경된 레이아웃 파라미터를 다시 설정
                    textViewFriendSpeed.layoutParams = layoutParams
                }

            }
        }

        userViewModel.userProfileImage.observe(viewLifecycleOwner){ uri ->
            if(uri != null){
                Glide.with(mainActivity).load(uri)
                    .into(fragmentMyInfoMainBinding.imageViewMyInfoMainProfile)
            }
            else{
                fragmentMyInfoMainBinding.imageViewMyInfoMainProfile.setImageResource(R.drawable.person_24px)
            }
        }
    }
}