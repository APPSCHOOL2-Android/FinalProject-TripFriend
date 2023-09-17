package com.test.tripfriend.repository

import android.net.Uri
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class UserRepository {

    //유저 정보 가져오는 함수
    suspend fun getTargetUserData(targetUserEmail:String, targetUserAuthentication:String) : QuerySnapshot{
        //파이어스토어 객체 얻기
        val firestore = Firebase.firestore

        //인자로 받은 이메일 값과 일치하는 필드
        return firestore.collection("User")
            .whereEqualTo("userEmail", targetUserEmail)
            .whereEqualTo("userAuthentication", targetUserAuthentication)
            .get().await()
    }

    //유저 프로필 이미지 url 가져오는 함수
    suspend fun getTargetUserProfileImage(targetUserProfileImagePath : String) : Uri {
        val storage = Firebase.storage

        //인자로 전달된 profileImagePath의 경로 형태 확인 필
        val fileRef = storage.reference.child(targetUserProfileImagePath)
        return fileRef.downloadUrl.await()
    }

    //푸시 알람 설정 여부 업데이트하는 함수
    suspend fun updateTargetUserPushNotification(documentId : String, pushNotificationState : Boolean){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .update("userPushNotification",pushNotificationState).await()
    }

    //채팅 알람 설정 여부 업데이트하는 함수
    suspend fun updateTargetUserChatNotification(documentId : String, chatNotificationState : Boolean){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .update("userChatNotification",chatNotificationState).await()
    }

    fun deleteTargetUserData(documentId: String){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .delete()
    }
}