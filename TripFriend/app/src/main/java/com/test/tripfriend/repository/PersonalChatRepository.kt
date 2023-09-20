package com.test.tripfriend.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.test.tripfriend.dataclassmodel.PersonalChatRoom
import com.test.tripfriend.dataclassmodel.PersonalChatting
import kotlinx.coroutines.tasks.await

class PersonalChatRepository {
    //파이어스토어 인스턴스 객체
    private val db = FirebaseFirestore.getInstance()

    //문의하기 클릭시 채팅방을 생성하는 메서드
    fun inquiryToPersonalChatRoom(
        personalChatRoom: PersonalChatRoom,
        callback: (Task<DocumentReference>) -> Unit,
    ) {
        db.collection("PersonalChatRoom").add(personalChatRoom).addOnCompleteListener(callback)
    }

    //내가 속한 채팅방에서 상대방의 정보를 가져오는 메서드
    suspend fun getPersonalChatInfo(myEmail: String): QuerySnapshot {
        val infoRef = db.collection("User").whereEqualTo("userEmail", myEmail).get().await()

        return infoRef
    }

    //내가 속해있는 개인채팅방을 모두 불러오는 메서드
    suspend fun getMyPersonalChatRoom(myEmail: String): QuerySnapshot {
        val chatRoomRef = db.collection("PersonalChatRoom").where(
            Filter.or(
                Filter.equalTo("personalChatPostWriterEmail", myEmail),
                Filter.equalTo("personalChatRequesterEmail", myEmail)
            )
        ).get().await()

        return chatRoomRef
    }

    //채팅방에 참여한 사람들 이메일 가져오기
    suspend fun getchatMember(roomId:String): DocumentSnapshot? {
        val chatMemberRef = db.collection("PersonalChatRoom").document(roomId).get().await()

        return chatMemberRef
    }

    //가장 죄근 채팅 가져오기
    suspend fun getLastChat(documentId: String): QuerySnapshot {
        val lastChat =
            db.collection("PersonalChatRoom").document(documentId).collection("PersonalChatting")
                .orderBy("personalChatSendTimeStamp", Query.Direction.DESCENDING).limit(1).get()
                .await()

        return lastChat
    }

    //채팅 db에 저장하기
    fun saveMyContentToDB(documentId:String,personalChatting: PersonalChatting){
        db.collection("PersonalChatRoom").document(documentId).collection("PersonalChatting").add(personalChatting)
    }

    //채팅 가져오기
    fun getChatting(documentId:String, callback: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit){
        val lastChat= db.collection("PersonalChatRoom").document(documentId).collection("PersonalChatting").orderBy("personalChatSendTimeStamp",Query.Direction.ASCENDING).addSnapshotListener(callback)
    }

    //유저 프로필 이미지 url 가져오는 함수
    suspend fun getUserProfileImage(userProfileImagePath : String) : Uri {
        val storage = Firebase.storage

        //인자로 전달된 profileImagePath의 경로 형태 확인 필
        val fileRef = storage.reference.child(userProfileImagePath)
        return fileRef.downloadUrl.await()
    }

    //문서 아이디를 통해 채팅방 삭제
    fun removePersonalChatRoomById(roomId:String){
        db.collection("PersonalChatRoom").document(roomId).delete()
    }
}