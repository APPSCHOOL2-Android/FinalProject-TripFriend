package com.test.tripfriend.dataclassmodel

data class TripReview (
    val tripReviewWriterEmail : String,         //동행 후기 작성자 이메일
    val tripReviewScore : Double,               //여행 점수
    val tripReviewStyle : List<String>,         //여행 스타일
    val tripReviewPostId : String,              //동행글 id (document id)
    val tripReviewTargetUserEmail : String      //후기 주인 이메일
)