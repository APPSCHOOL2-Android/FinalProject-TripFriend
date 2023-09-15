package com.test.tripfriend.repository

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserRepository {

    suspend fun getTargetUserData(targetUserEmail:String, targetUserAuthentication:String) : QuerySnapshot{
        //파이어스토어 객체 얻기
        val firestore = Firebase.firestore

        //인자로 받은 이메일 값과 일치하는 필드
        return firestore.collection("User")
            .whereEqualTo("userEmail", targetUserEmail)
            .whereEqualTo("userAuthentication", targetUserAuthentication)
            .get().await()
    }

}