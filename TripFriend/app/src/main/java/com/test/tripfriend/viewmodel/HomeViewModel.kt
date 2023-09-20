package com.test.tripfriend.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.repository.HomeListRepository
import com.test.tripfriend.repository.TripPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {
    val homeListRepository = HomeListRepository()

    val tripPostList = MutableLiveData<List<TripPost>>()

    fun getTripPostData() {
//        val currentTripPostSnapshot = homeListRepository.getDocumentData()

//        if (currentTripPostSnapshot != null) {
//            val currentTripPost = currentTripPostSnapshot.toObjects(TripPost::class.java)
//
//            tripPostList.value = currentTripPost
//        }

        val homePostInfoList= mutableListOf<DocumentSnapshot>()
        val resultList = mutableListOf<TripPost>()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val currentTripPostSnapshot = async { homeListRepository.getDocumentData() }

            homePostInfoList.addAll(currentTripPostSnapshot.await().documents)

            withContext(Dispatchers.Main) {
                for(document in homePostInfoList) {
                    val tripPostObj = document.toObject(TripPost::class.java)

                    if (tripPostObj != null) {
                        tripPostObj.tripPostDocumentId = document.id
                        resultList.add(tripPostObj)
                    }
                }

            }

            withContext(Dispatchers.Main) {
                tripPostList.value = resultList
            }

            scope.cancel()

        }
    }

}