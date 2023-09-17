package com.test.tripfriend.dataclassmodel

data class GroupChatRoom(
    val groupChatPostWriterEmail : String,
    val groupChatMemberEmailList : List<String>,
    val groupChatTripPostId : String
)

data class GroupChatting(
    val groupChatWriterEmail : String,
    val groupChatContent : String,
    val groupChatSendDateAndTime : String
)