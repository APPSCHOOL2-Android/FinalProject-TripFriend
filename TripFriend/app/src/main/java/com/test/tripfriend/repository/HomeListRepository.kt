package com.test.tripfriend.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

class HomeListRepository {

    val db = FirebaseFirestore.getInstance()

    // 문서 가져오기 메서드
    suspend fun getDocumentData(): QuerySnapshot {
        val docRef =
            db.collection("TripPost").orderBy("tripPostIdx", Query.Direction.DESCENDING).get()
                .await()

        return docRef
    }

}