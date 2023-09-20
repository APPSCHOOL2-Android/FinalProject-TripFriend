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
import com.test.tripfriend.databinding.FragmentMoreNaverInfoInputBinding
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.UserRepository
import com.test.tripfriend.ui.main.MainActivity

class MoreNaverInfoInputFragment : Fragment() {
    lateinit var fragmentMoreNaverInfoInputBinding: FragmentMoreNaverInfoInputBinding
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
        fragmentMoreNaverInfoInputBinding = FragmentMoreNaverInfoInputBinding.inflate(layoutInflater)

        val checkStateAutoLogin = arguments?.getBoolean("check")

        fragmentMoreNaverInfoInputBinding.run {
            materialToolbarMoreNaverInfoInputToolbarTitle.run {
                setNavigationIcon(R.drawable.arrow_back_24px)
                setNavigationIconTint(getResources().getColor(R.color.black))
                setNavigationOnClickListener {
                    loginMainActivity.removeFragment(LoginMainActivity.MORE_NAVER_INFO_INPUT_FRAGMENT)
                }
            }
            textInputEditTextMoreNaverInfoInputUserPhoneNumber.setText(loginMainActivity.userPhoneNumber)

            textInputEditTexttMoreNaverInfoInputUserNickname.setText(loginMainActivity.userNickname)

            buttonMoreNaverInfoInputSelectMBTI.run {
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
            buttonMoreNaverInfoInputSave.run {
                val drawableInfo = resources.getDrawable(R.drawable.info_24px)
                val drawableDone = resources.getDrawable(R.drawable.done_24px)
                drawableInfo?.setColorFilter(ContextCompat.getColor(loginMainActivity,R.color.highLightColor), PorterDuff.Mode.SRC_IN)
                drawableDone?.setColorFilter(ContextCompat.getColor(loginMainActivity,R.color.highLightColor), PorterDuff.Mode.SRC_IN)

                setOnClickListener {
                    var checkNullNickname = 0
                    var checkNullPhoneNumber = 0
                    var checkNullMBTI = 0
                    var checkPhoneNum = 0
                    var checkNickname = 0

                    //전화번호 안적었으면
                    if(textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString() == ""){
                        checkNullPhoneNumber = 0
                        textViewCheckPhoneNumberNaver.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                        textViewCheckPhoneNumberNaver.text = "\"-\"없이 휴대폰 번호를 입력해주세요."
                    }
                    else{
                        checkNullPhoneNumber = 1
                        if(
                            textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString().length != 11 || //번호 개수가 안맞을 때
                            textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString().startsWith("010") == false|| // 010으로 시작 안할 때
                            textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString().contains('-')) //-를 포함했을 때
                        {
                            textViewCheckPhoneNumberNaver
                                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                            textViewCheckPhoneNumberNaver.text = "올바른 형식의 번호를 입력해주세요."
                            checkPhoneNum = 0
                        }
                        else {
                            UserRepository.getAllUser {
                                for (document in it.result.documents) {
                                    //중복된 번호가 있을 경우
                                    if (textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString()
                                        == document.getString("userPhoneNum")
                                    ) {
                                        textViewCheckPhoneNumberNaver.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                        textViewCheckPhoneNumberNaver.text = "이미 사용중인 번호입니다."
                                        checkPhoneNum = 0
                                        break
                                    }
                                    else{
                                        textViewCheckPhoneNumberNaver.text = "사용 가능한 번호입니다."
                                        checkPhoneNum = 1
                                        textViewCheckPhoneNumberNaver.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                    }
                                }

                                //닉네임 체크
                                if(textInputEditTexttMoreNaverInfoInputUserNickname.text.toString() == ""){
                                    checkNullNickname = 0
                                    textViewCheckNicknameNaver
                                        .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                    textViewCheckNicknameNaver.text = "닉네임을 입력해주세요."
                                }
                                else{
                                    checkNullNickname = 1
                                    UserRepository.getAllUser {
                                        for (document in it.result.documents) {
                                            //중복된 닉네임 있을경우
                                            if (textInputEditTexttMoreNaverInfoInputUserNickname.text.toString()
                                                == document.getString("userNickname")
                                            ) {
                                                checkNickname = 0
                                                textViewCheckNicknameNaver
                                                    .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                                textViewCheckNicknameNaver.text = "이미 사용중인 닉네임입니다."
                                                break
                                            } else {
                                                textViewCheckNicknameNaver.text = "사용 가능한 닉네임입니다."
                                                checkNickname = 1
                                                textViewCheckNicknameNaver
                                                    .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                            }
                                        }
                                        if(buttonMoreNaverInfoInputSelectMBTI.text == "MBTI"){
                                            textViewCheckMBTINaver.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                            checkNullMBTI = 0
                                        }
                                        else if(buttonMoreNaverInfoInputSelectMBTI.text == "모름"){
                                            val builder= MaterialAlertDialogBuilder(loginMainActivity,R.style.DialogTheme).apply {
                                                setTitle("MBTI 선택")
                                                setMessage("MBTI를 선택하면 다른 사용자들에게 더 많은 정보를 제공할 수 있습니다.")
                                                setNegativeButton("모름으로 선택"){ dialogInterface: DialogInterface, i: Int ->
                                                    checkNullMBTI = 1
                                                    textViewCheckMBTINaver
                                                        .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                                    textViewCheckMBTINaver.text = "선택하신 MBTI는 ${buttonMoreNaverInfoInputSelectMBTI.text}입니다."
                                                    if(checkNullPhoneNumber == 1 && checkNullNickname == 1 &&
                                                        checkNullMBTI == 1 && checkPhoneNum == 1 && checkNickname == 1){
                                                        val userClass = User(
                                                            loginMainActivity.userAuth,
                                                            loginMainActivity.userEmail,
                                                            loginMainActivity.userPw,
                                                            textInputEditTexttMoreNaverInfoInputUserNickname.text.toString(),
                                                            loginMainActivity.userName,
                                                            textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString(),
                                                            buttonMoreNaverInfoInputSelectMBTI.text.toString()
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
                                                    buttonMoreNaverInfoInputSelectMBTI.text = "MBTI"
                                                    textViewCheckMBTINaver.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableInfo, null, null, null)
                                                    textViewCheckMBTINaver.text = "MBTI를 선택해주세요."
                                                    checkNullMBTI = 0
                                                }
                                            }
                                            builder.show()
                                        }
                                        else{
                                            textViewCheckMBTINaver
                                                .setCompoundDrawablesRelativeWithIntrinsicBounds(drawableDone, null, null, null)
                                            textViewCheckMBTINaver.text = "선택하신 MBTI는 ${buttonMoreNaverInfoInputSelectMBTI.text}입니다."
                                            checkNullMBTI = 1
                                            if(checkNullPhoneNumber == 1 && checkNullNickname == 1 &&
                                                checkNullMBTI == 1 && checkPhoneNum == 1 && checkNickname == 1){
                                                val userClass = User(
                                                    loginMainActivity.userAuth,
                                                    loginMainActivity.userEmail,
                                                    loginMainActivity.userPw,
                                                    textInputEditTexttMoreNaverInfoInputUserNickname.text.toString(),
                                                    loginMainActivity.userName,
                                                    textInputEditTextMoreNaverInfoInputUserPhoneNumber.text.toString(),
                                                    buttonMoreNaverInfoInputSelectMBTI.text.toString()
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
                                                            if(userClass.userEmail == document.getString("userEmail")) {
                                                                if(userClass.userAuthentication == document.getString("userAuthentication")){
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
                                                                else continue
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
                }
            }
        }

        return fragmentMoreNaverInfoInputBinding.root
    }
}