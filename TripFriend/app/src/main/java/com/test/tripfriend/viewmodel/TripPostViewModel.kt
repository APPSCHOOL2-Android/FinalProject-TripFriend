package com.test.tripfriend.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.repository.TripPostRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class TripPostViewModel: ViewModel() {
    val tripPostRepository = TripPostRepository()
    val tripPostInProgressList = MutableLiveData<List<TripPost>>()
    val tripPostPassList = MutableLiveData<List<TripPost>>()

    val tripPostList = MutableLiveData<TripPost>()
    val tripPostLiked = MutableLiveData<Int>()

    val tripPostImage = MutableLiveData<Uri>()

    // 오늘 날짜
    val currentTime : Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    val today = dataFormat.format(currentTime).toInt()

    private val _tripPost = MutableLiveData<TripPost?>()
    val tripPost : LiveData<TripPost?>
        get() = _tripPost

    // 오늘 날짜 기준으로 참여/지난 동행글 구분하여 데이터 추출
    fun getAllTripPostData(userEmail: String) {
        val tripPostInfoList= mutableListOf<DocumentSnapshot>()
        val resultInProgressList = mutableListOf<TripPost>()
        val resultPassList = mutableListOf<TripPost>()

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val currentTripPostSnapshot = async { tripPostRepository.getAllDocumentData(userEmail) }
            tripPostInfoList.addAll(currentTripPostSnapshot.await().documents)

            for(document in tripPostInfoList) {
                val data = document.toObject(TripPost::class.java)
                var endDate = data?.tripPostDate

                // 참여중인 동행글
                if(today <= endDate!![1].toInt()) {
                    data!!.tripPostDocumentId = document.id

                    resultInProgressList.add(data)
                } else { // 지난 동행글
                    data!!.tripPostDocumentId = document.id

                    resultPassList.add(data)
                }
            }

            withContext(Dispatchers.Main) {
                tripPostInProgressList.value = resultInProgressList
                tripPostPassList.value = resultPassList
            }
            scope.cancel()
        }
    }

    // 문서id로 접근하여 데이터 가져오기
    fun getSelectDocumentData(documentId: String) {
        val scope = CoroutineScope(Dispatchers.Default)
        var result: Int

        scope.launch {
            var resultData = TripPost()
            val currentTripPostSnapshot = async { tripPostRepository.getSelectDocumentData(documentId) }
            val data = currentTripPostSnapshot.await().toObject(TripPost::class.java)

            result = data!!.tripPostLiked!!.size

            if(data != null) {
                resultData = data
            }

            withContext(Dispatchers.Main) {
                tripPostList.value = resultData
                tripPostLiked.value = result
            }

            scope.cancel()
        }
    }

    // 동행글 이미지 가져오기
    fun getTargetUserProfileImage(tripPostImagePath : String){
        val imageUri = runBlocking { tripPostRepository.getTripPostImage(tripPostImagePath) }

        if(imageUri != null){
            tripPostImage.value = imageUri
        }
    }

    fun getTargetUserTripPost(tripPostDocumentId : String){
        val tripPostDocumentSnapshot =
            runBlocking {
                tripPostRepository.getTargetUserTripPost(tripPostDocumentId)
            }

        if(tripPostDocumentSnapshot != null){
            val currentTripPost = tripPostDocumentSnapshot.toObject(TripPost::class.java)

            //현재 날짜 구하기
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val date : Long = currentDate.format(formatter).toLong()

            if(currentTripPost != null){
                if(currentTripPost.tripPostDate!![0].toLong() > date){
                    _tripPost.value = currentTripPost!!
                }
                else{
                    _tripPost.value = null
                }
            }
        }
    }
}