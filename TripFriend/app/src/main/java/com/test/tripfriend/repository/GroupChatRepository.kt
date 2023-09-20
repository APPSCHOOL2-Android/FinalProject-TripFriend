package com.test.tripfriend.repository

import android.provider.ContactsContract.CommonDataKinds.Nickname
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.test.tripfriend.dataclassmodel.GroupChatRoom
import com.test.tripfriend.dataclassmodel.GroupChatting
import com.test.tripfriend.dataclassmodel.PersonalChatting
import kotlinx.coroutines.tasks.await

class GroupChatRepository {
    //파이어스토어 인스턴스 객체
    private val db = FirebaseFirestore.getInstance()

    //단체 채팅방을 생성하는 메서드
    fun creatToGroupChatRoom(groupChatRoom: GroupChatRoom, callback: (Task<DocumentReference>) -> Unit, ) {
        db.collection("PersonalChatRoom").add(groupChatRoom).addOnCompleteListener(callback)
    }

    //내가 속해있는 단체채팅방을 모두 불러오는 메서드
    suspend fun getMyGroupChatRoom(myNickname:String): QuerySnapshot {
        val chatRoomRef = db.collection("GroupChatRoom").whereArrayContains("groupChatMemberNicknameList",myNickname).get().await()

        return chatRoomRef
    }

    //내가 속한 채팅방에서 상대방의 정보를 가져오는 메서드
    suspend fun getGroupUserInfo(myEmail: String): QuerySnapshot {
        val infoRef = db.collection("User").whereEqualTo("userEmail", myEmail).get().await()

        return infoRef
    }

    //해당 채팅방에 띄울 정보를 가져오는 용도(타이틀 가져와야함)
    suspend fun getRoomInfoFromPost(tripPostId:String):DocumentSnapshot{
        val postInfoRef = db.collection("TripPost").document(tripPostId).get().await()

        return postInfoRef
    }

    //가장 죄근 채팅 가져오기
    suspend fun getLastChat(tripPostId: String): QuerySnapshot {
        val lastChat =
            db.collection("GroupChatRoom").document(tripPostId).collection("GroupChatting")
                .orderBy("groupChatSendTimeStamp", Query.Direction.DESCENDING).limit(1).get()
                .await()

        return lastChat
    }

    //채팅 가져오기
    fun getChatting(documentId:String, callback: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit){
        val lastChat= db.collection("GroupChatRoom").document(documentId).collection("GroupChatting").orderBy("groupChatSendTimeStamp",Query.Direction.ASCENDING).addSnapshotListener(callback)
    }

    //유저의 닉네임에 맞는 정보 가져오기
    suspend fun getUserInfoByNickname(nickName:String):QuerySnapshot{
        val infoRef = db.collection("User").whereEqualTo("userNickname", nickName).get().await()

        return infoRef
    }

    //채팅 저장
    fun saveMyContentToDB(documentId:String,groupChatting: GroupChatting){
        db.collection("GroupChatRoom").document(documentId).collection("GroupChatting").add(groupChatting)
    }

    suspend fun getGroupChatDocumentId(tripPostDocumentId: String) : QuerySnapshot{
        return db.collection("GroupChatRoom")
            .whereEqualTo("groupChatTripPostId", tripPostDocumentId)
            .get().await()
    }

    fun addGroupChatMemberNickname(userNickname : String, groupChatDocumentId : String){
        db.collection("GroupChatRoom")
            .document(groupChatDocumentId)
            .update("groupChatMemberNicknameList", FieldValue.arrayUnion(userNickname))
    }

}