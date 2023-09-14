package com.test.tripfriend.dataclassmodel

data class PersonalChat(
    val personalChatPostWriterEmail : String,       //동행글 작성자 이메일
    val personalChatRequesterEmail : String         //요청자 이메일
)

data class PersonalChatting(
    val personalChatWriterEmail : String,           //채팅 작성자 이메일
    val personalChatContent : String,               //채팅 내용
    val personalChatSendDateAndTime : String        //채팅 작성 날짜 및 시간
)