package com.test.tripfriend.dataclassmodel

data class User (
    val userAuthentication : String,                //인증 수단
    val userEmail : String,                         //이메일
    val userPw : String,                            //비밀번호
    val userNickname : String,                      //닉네임
    val userName : String,                          //이름
    val userPhoneNum : String,                      //전화번호
    val userMBTI : String,                          //MBTI
    val userProfilePath : String = "",              //프로필 사진
    val userFriendSpeed : Double = 10.0,            //친구속도
    val userTripScore : Double = 0.0,               //동행 점수
    val userTripCount : Int = 0,                    //동행 횟수
    val userChatNotification : Boolean = true,      //채팅 알림 여부
    val userPushNotification : Boolean = true       //푸시 알림 여부
)