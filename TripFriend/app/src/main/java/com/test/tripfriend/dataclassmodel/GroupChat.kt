package com.test.tripfriend.dataclassmodel

data class GroupChat(
    val groupChatPostWriterEmail : String,
    val groupChatMemberEmailList : List<String>,
    val groupChatTripPostId : String
)

data class GroupChatting(
    val groupChatWriterEmail : String,
    val groupChatContent : String,
    val groupChatSendDateAndTime : String
)