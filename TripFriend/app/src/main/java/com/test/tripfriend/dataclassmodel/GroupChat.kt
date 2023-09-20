package com.test.tripfriend.dataclassmodel

data class GroupChatRoom(
    val groupChatPostWriterEmail: String?=null,
    val groupChatTripPostId: String?=null,
    val groupChatMemberNicknameList: List<String> = emptyList()
)

data class GroupChatting(
    val groupChatWriterEmail : String?=null,
    val groupChatContent : String?=null,
    val groupChatSendDateAndTime : String?=null,
    val groupChatSendTimeStamp: Long?=null
)

class UserInfo{
    val userEmail:String?=null
    val userProfilePath:String?=null
}

class PostInfo{
    val tripPostMemberList:List<String>?=null
    val tripPostTitle:String?=null
}
class GroupChatInfo {
    var tripPostTitle:String?=null
    var tripPostId:String?=null
    var roomId: String? = null
    var memberCount:Int?=null
    var lastChatContent: String? = null
    var lastChatDate: String? = null
    var groupChatPostWriterEmail:String?=null
    var groupChatMemberNicknameList: List<String> = emptyList()
}