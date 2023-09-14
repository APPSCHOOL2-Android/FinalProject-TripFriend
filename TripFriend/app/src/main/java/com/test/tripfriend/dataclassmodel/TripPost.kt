package com.test.tripfriend.dataclassmodel

data class TripPost (
    val tripPostWriterEmail : String,               //작성자이메일
    val tripPostTitle : String,                     //제목
    val tripPostMemberList : List<String>,          //참여자 이메일 리스트
    val tripPostMemberCount : Int,                  //인원수
    val tripPostImage : String,                     //사진
    val tripPostDate : List<Int>,                   //날짜 idx 0-시작날짜, 1-마지막날짜
    val tripPostLocationName : String,              //지역명
    val tripPostLatitude : Double,                  //위도
    val tripPostLongitude : Double,                 //경도
    val tripPostLikedCount : Int,                   //찜 개수
    val tripPostTripCategory : List<String>,        //여행 카테고리 (3개)
    val tripPostHashTag : String,                   //해시태그
    val tripPostContent : String                    //내용
)