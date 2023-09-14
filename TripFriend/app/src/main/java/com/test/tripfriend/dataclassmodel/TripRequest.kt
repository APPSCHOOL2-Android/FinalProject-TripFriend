package com.test.tripfriend.dataclassmodel

data class TripRequest(
    val tripRequestPostId : String,     //동행글 id (document id)
    val tripRequestEmail : String,      //신청자 이메일
    val tripRequestCotent : String,     //자기소개 내용
    val tripRequestState : String       //수락 상태
)