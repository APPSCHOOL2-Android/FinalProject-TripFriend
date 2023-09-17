package com.test.tripfriend.repository

import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TripReviewRepository {

    suspend fun getTargetUserReviews(targetUserEmail : String) : QuerySnapshot{
        val firestore = Firebase.firestore

        return firestore.collection("TripReview")
            .whereEqualTo("tripReviewTargetUserEmail", targetUserEmail)
            .get().await()
    }

    suspend fun getReviewWriterNickname(reviewWriterEmail : String) : QuerySnapshot{
        val firestore = Firebase.firestore

        return firestore.collection("User")
            .whereEqualTo("userEmail", reviewWriterEmail)
            .get().await()
    }

}