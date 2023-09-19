package com.test.tripfriend.ui.user

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.test.tripfriend.R
import com.test.tripfriend.databinding.FragmentMoreKakaoInfoInputBinding
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.main.MainActivity

class MoreKakaoInfoInputFragment : Fragment() {
    lateinit var fragmentMoreKakaoInfoInputBinding: FragmentMoreKakaoInfoInputBinding
    lateinit var loginMainActivity: LoginMainActivity
    val mbtiList = arrayOf(
        "ISTJ","ISFJ","ISTP","ISFP",
        "INFJ","INTJ","INTP","INFP",
        "ESTP","ESFP","ESTJ","ESFJ",
        "ENTP","ENFP","ENTJ","ENFJ","모름"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginMainActivity = activity as LoginMainActivity
        fragmentMoreKakaoInfoInputBinding = FragmentMoreKakaoInfoInputBinding.inflate(layoutInflater)

        val checkStateAutoLogin = arguments?.getBoolean("check")

        //이름 폰번 MBTI입력 받고 닉네임 중복 확인
        fragmentMoreKakaoInfoInputBinding.run {
            materialToolbarMoreKakaoInfoInput.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationIconTint(getResources().getColor(R.color.black))
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.MORE_KAKAO_INFO_INPUT_FRAGMENT)
                }
            }
            textInputEditTextMoreKakaoInfoInputUserName.run {
            }
            textInputEditTextMoreKakaoInfoInputUserPhoneNumber.run {

            }
            textInputEditTexttMoreKakaoInfoInputUserNickname.run {
                setText(loginMainActivity.userNickname)
            }
            buttonMoreKakaoInfoInputSelectMBTI.run {
                setOnClickListener {
                    val builder = MaterialAlertDialogBuilder(loginMainActivity)
                    builder.setTitle("MBTI")
                    builder.setItems(mbtiList) { dialogInterface: DialogInterface, i: Int ->
                        text = mbtiList[i]
                    }
                    builder.setNegativeButton("취소", null)
                    val alertDialog = builder.create()
                    alertDialog.show()

                    // Negative 버튼의 글자 색상 변경
                    val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negativeButton?.setTextColor(resources.getColor(R.color.black))
                }
            }
            buttonMoreKakaoInfoInputSave.run {
                setOnClickListener {
                    var checkNullName = 1
                    var checkNullPhoneNumber = 1
                    var checkNullNickname = 1
                    var checkNullMBTI = 1
                    var checkPhoneNum = 1
                    var checkNickname = 1

                    val drawableInfo = resources.getDrawable(R.drawable.info_24px)
                    val drawableDone = resources.getDrawable(R.drawable.done_24px)
                    drawableInfo?.setColorFilter(ContextCompat.getColor(loginMainActivity,R.color.highLightColor),PorterDuff.Mode.SRC_IN)
                    drawableDone?.setColorFilter(ContextCompat.getColor(loginMainActivity,R.color.highLightColor),PorterDuff.Mode.SRC_IN)

                    //이름 검사
                    if(textInputEditTextMoreKakaoInfoInputUserName.text.toString() == ""){
                        checkNullName = 0
                        textViewCheckName.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                        textViewCheckName.text = "이름을 입력해주세요."
                    } else{
                        checkNullName = 1
                        textViewCheckName.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                        textViewCheckName.text = "사용 가능한 이름입니다."
                    }

                    //휴대폰 번호 검사
                    if(textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString() == ""){
                        checkNullPhoneNumber = 0
                        textViewCheckPhoneNumber.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                        textViewCheckPhoneNumber.text = "\"-\"없이 휴대폰 번호를 입력해주세요."
                    } else{
                        checkNullPhoneNumber = 1
                        UserRepository.getAllUser {
                            for (document in it.result.documents) {
                                //중복된 번호가 있을 경우
                                if(textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString()
                                    == document.getString("userPhoneNum"))
                                {
                                    textViewCheckPhoneNumber
                                        .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                    textViewCheckPhoneNumber.text = "이미 사용중인 번호입니다."
                                    checkPhoneNum = 0
                                    break
                                }
                                else{
                                    if(
                                        textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString().length != 11 || //번호 개수가 안맞을 때
                                        textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString().startsWith("010") == false|| // 010으로 시작 안할 때
                                        textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString().contains('-')) //-를 포함했을 때
                                    {
                                        textViewCheckPhoneNumber
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                        textViewCheckPhoneNumber.text = "올바른 형식의 번호를 입력해주세요."
                                        checkPhoneNum = 0
                                    }
                                    else {
                                        textViewCheckPhoneNumber.text = "사용 가능한 번호입니다."
                                        checkPhoneNum = 1
                                        textViewCheckPhoneNumber
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                drawableDone,
                                                null,
                                                null,
                                                null
                                            )
                                    }
                                }
                            }
                            //닉네임 검사
                            if(textInputEditTexttMoreKakaoInfoInputUserNickname.text.toString() == ""){
                                checkNullNickname = 0
                                textViewCheckNickname
                                    .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                textViewCheckNickname.text = "닉네임을 입력해주세요."
                            } else{
                                checkNullNickname = 1
                                UserRepository.getAllUser {
                                    for (document in it.result.documents) {
                                        //중복된 닉네임 있을경우
                                        if (textInputEditTexttMoreKakaoInfoInputUserNickname.text.toString()
                                            == document.getString("userNickname"))
                                        {
                                            checkNickname = 0
                                            textViewCheckNickname
                                                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                            textViewCheckNickname.text = "이미 사용중인 닉네임입니다."
                                            break
                                        }
                                        else{
                                            textViewCheckNickname.text = "사용 가능한 닉네임입니다."
                                            checkNickname = 1
                                            textViewCheckNickname
                                                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                        }
                                    }
                                    //MBTI 검사
                                    if(buttonMoreKakaoInfoInputSelectMBTI.text == "MBTI"){
                                        textViewCheckMBTI.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                        checkNullMBTI = 0
                                    }
                                    else if(buttonMoreKakaoInfoInputSelectMBTI.text == "모름"){
                                        checkNullMBTI = 0
                                        val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                            setTitle("MBTI 선택")
                                            setMessage("MBTI를 선택하면 다른 사용자들에게 더 많은 정보를 제공할 수 있습니다.")
                                            setNegativeButton("모름으로 선택"){ dialogInterface: DialogInterface, i: Int ->
                                                checkNullMBTI = 1
                                                textViewCheckMBTI
                                                    .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                                textViewCheckMBTI.text = "선택하신 MBTI는 ${buttonMoreKakaoInfoInputSelectMBTI.text}입니다."
                                                if(checkNullName == 1 && checkNullPhoneNumber == 1 && checkNullNickname == 1 &&
                                                    checkNullMBTI == 1 && checkPhoneNum == 1 && checkNickname == 1){
                                                    val userClass = User(
                                                        loginMainActivity.userAuth,
                                                        loginMainActivity.userEmail,
                                                        loginMainActivity.userPw,
                                                        textInputEditTexttMoreKakaoInfoInputUserNickname.text.toString(),
                                                        textInputEditTextMoreKakaoInfoInputUserName.text.toString(),
                                                        textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString(),
                                                        buttonMoreKakaoInfoInputSelectMBTI.text.toString()
                                                    )

                                                    //데이터 저장
                                                    UserRepository.addUser(userClass){
                                                        loginMainActivity.userNickname = ""
                                                        loginMainActivity.userPhoneNumber = ""
                                                        loginMainActivity.userEmail = ""
                                                        loginMainActivity.userName = ""
                                                        loginMainActivity.userAuth = ""
                                                        loginMainActivity.userMBTI = ""
                                                        loginMainActivity.userPw = ""
                                                        UserRepository.getAllUser {
                                                            for (document in it.result.documents) {
                                                                if(userClass.userEmail == document.getString("userEmail") &&
                                                                    userClass.userAuthentication == document.getString("userAuthentication")){
                                                                    val userAuthentication= document.getString("userAuthentication").toString()
                                                                    val userEmail = document.getString("userEmail").toString()
                                                                    val userPw = document.getString("userPw").toString()
                                                                    val userNickname= document.getString("userNickname").toString()
                                                                    val userName= document.getString("userName").toString()
                                                                    val userPhoneNum= document.getString("userPhoneNum").toString()
                                                                    val userMBTI= document.getString("userMBTI").toString()
                                                                    val userProfilePath= document.getString("userProfilePath").toString()
                                                                    val userFriendSpeed= document.getDouble("userFriendSpeed")!!
                                                                    val userTripScore= document.getDouble("userTripScore")!!
                                                                    val userTripCount= document.getLong("userTripCount")?.toInt()!!
                                                                    val userChatNotification = document.getBoolean("userChatNotification")!!
                                                                    val userPushNotification= document.getBoolean("userPushNotification")!!

                                                                    val userClass = User(
                                                                        userAuthentication,
                                                                        userEmail,
                                                                        userPw ,
                                                                        userNickname ,
                                                                        userName,
                                                                        userPhoneNum ,
                                                                        userMBTI,
                                                                        userProfilePath = userProfilePath,
                                                                        userFriendSpeed = userFriendSpeed,
                                                                        userTripScore = userTripScore,
                                                                        userTripCount = userTripCount,
                                                                        userChatNotification = userChatNotification,
                                                                        userPushNotification = userPushNotification
                                                                    )
                                                                    val checkStateAutoLogin = checkStateAutoLogin!!

                                                                    val sharedPreferences =
                                                                        loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                                                                    UserRepository.saveUserInfo(sharedPreferences,userClass,checkStateAutoLogin)

                                                                    val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                                                        setTitle("로그인 성공")
                                                                        setMessage("로그인에 성공하였습니다. 트립친과 함께 좋은 여행이 되길 바랍니다.^^")
                                                                        setNegativeButton("메인화면으로",null)
                                                                        setOnDismissListener{
                                                                            val intent = Intent(loginMainActivity, MainActivity::class.java)
                                                                            startActivity(intent)
                                                                            loginMainActivity.finish()
                                                                        }
                                                                    }
                                                                    builder.show()
                                                                    break
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            setPositiveButton("MBTI 선택"){ dialogInterface: DialogInterface, i: Int ->
                                                buttonMoreKakaoInfoInputSelectMBTI.text = "MBTI"
                                                textViewCheckMBTI.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                                textViewCheckMBTI.text = "MBTI를 선택해주세요."
                                                checkNullMBTI = 0
                                            }
                                        }
                                        builder.show()

                                    }
                                    else if(buttonMoreKakaoInfoInputSelectMBTI.text != "MBTI" && buttonMoreKakaoInfoInputSelectMBTI.text != "모름"){
                                        textViewCheckMBTI
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                        textViewCheckMBTI.text = "선택하신 MBTI는 ${buttonMoreKakaoInfoInputSelectMBTI.text}입니다."
                                        checkNullMBTI = 1

                                        if(checkNullName == 1 && checkNullPhoneNumber == 1 && checkNullNickname == 1 && checkNullMBTI == 1) {
                                            if (checkPhoneNum == 1 && checkNickname == 1) {
                                                val userClass = User(
                                                    loginMainActivity.userAuth,
                                                    loginMainActivity.userEmail,
                                                    loginMainActivity.userPw,
                                                    textInputEditTexttMoreKakaoInfoInputUserNickname.text.toString(),
                                                    textInputEditTextMoreKakaoInfoInputUserName.text.toString(),
                                                    textInputEditTextMoreKakaoInfoInputUserPhoneNumber.text.toString(),
                                                    buttonMoreKakaoInfoInputSelectMBTI.text.toString()
                                                )

                                                //데이터 저장
                                                UserRepository.addUser(userClass){
                                                    loginMainActivity.userNickname = ""
                                                    loginMainActivity.userPhoneNumber = ""
                                                    loginMainActivity.userEmail = ""
                                                    loginMainActivity.userName = ""
                                                    loginMainActivity.userAuth = ""
                                                    loginMainActivity.userMBTI = ""
                                                    loginMainActivity.userPw = ""

                                                    UserRepository.getAllUser {
                                                        for (document in it.result.documents) {
                                                            if(userClass.userEmail == document.getString("userEmail") &&
                                                                userClass.userAuthentication == document.getString("userAuthentication")){
                                                                val userAuthentication= document.getString("userAuthentication").toString()
                                                                val userEmail = document.getString("userEmail").toString()
                                                                val userPw = document.getString("userPw").toString()
                                                                val userNickname= document.getString("userNickname").toString()
                                                                val userName= document.getString("userName").toString()
                                                                val userPhoneNum= document.getString("userPhoneNum").toString()
                                                                val userMBTI= document.getString("userMBTI").toString()
                                                                val userProfilePath= document.getString("userProfilePath").toString()
                                                                val userFriendSpeed= document.getDouble("userFriendSpeed")!!
                                                                val userTripScore= document.getDouble("userTripScore")!!
                                                                val userTripCount= document.getLong("userTripCount")?.toInt()!!
                                                                val userChatNotification = document.getBoolean("userChatNotification")!!
                                                                val userPushNotification= document.getBoolean("userPushNotification")!!

                                                                val userClass = User(
                                                                    userAuthentication,
                                                                    userEmail,
                                                                    userPw ,
                                                                    userNickname ,
                                                                    userName,
                                                                    userPhoneNum ,
                                                                    userMBTI,
                                                                    userProfilePath = userProfilePath,
                                                                    userFriendSpeed = userFriendSpeed,
                                                                    userTripScore = userTripScore,
                                                                    userTripCount = userTripCount,
                                                                    userChatNotification = userChatNotification,
                                                                    userPushNotification = userPushNotification
                                                                )
                                                                val checkStateAutoLogin = checkStateAutoLogin!!

                                                                val sharedPreferences =
                                                                    loginMainActivity.getSharedPreferences("user_info", Context.MODE_PRIVATE)
                                                                UserRepository.saveUserInfo(sharedPreferences,userClass,checkStateAutoLogin)

                                                                val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                                                    setTitle("로그인 성공")
                                                                    setMessage("로그인에 성공하였습니다. 트립친과 함께 좋은 여행이 되길 바랍니다.^^")
                                                                    setNegativeButton("메인화면으로",null)
                                                                    setOnDismissListener{
                                                                        val intent = Intent(loginMainActivity, MainActivity::class.java)
                                                                        startActivity(intent)
                                                                        loginMainActivity.finish()
                                                                    }
                                                                }
                                                                builder.show()
                                                                break
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                    }




                    //저장

                }
            }
        }

        return fragmentMoreKakaoInfoInputBinding.root
    }
}