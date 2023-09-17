package com.test.tripfriend.dataclassmodel

data class PersonalChatRoom(
    val personalChatPostWriterEmail: String,       //동행글 작성자 이메일
    val personalChatRequesterEmail: String,         //요청자 이메일
)

//서버에서 값을 가져오기 위한 생성자 없는 클래스
class PersonalChatRoom2 {
    val personalChatPostWriterEmail: String? = null       //동행글 작성자 이메일
    val personalChatRequesterEmail: String? = null         //요청자 이메일
}

data class PersonalChatting(
    val personalChatWriterEmail: String,           //채팅 작성자 이메일
    val personalChatContent: String,               //채팅 내용
    val personalChatSendDateAndTime: String, //채팅 작성 날짜 및 시간
    val personalChatSendTimeStamp: Long,   //채팅 저장 시기
)

//서버에서 값을 가져오기 위한 생성자 없는 클래스
class PersonalChatting2 {
    val personalChatWriterEmail: String? = null           //채팅 작성자 이메일
    val personalChatContent: String? = null               //채팅 내용
    val personalChatSendDateAndTime: String? = null //채팅 작성 날짜 및 시간
    val personalChatSendTimeStamp: Long? = null   //채팅 저장 시기
}

//1:1 채팅방 리스트에 필요한 정보를 저장하고 사용하기 위한 클래스
class PersonalChatInfo {
    var userEmail: String? = null
    var userProfilePath: String? = null
    var userNickname: String? = null
    var documentId: String? = null
    var lastChatContent: String? = null
    var lastChatDate: String? = null
}


