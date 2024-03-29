package com.test.tripfriend.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.dataclassmodel.TripRequest
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

class TripPostViewModel : ViewModel() {
    val tripPostRepository = TripPostRepository()
    val tripPostInProgressList = MutableLiveData<List<TripPost>>()
    val tripPostPassList = MutableLiveData<List<TripPost>>()
    val tripPostLikedList = MutableLiveData<List<TripPost>>()

    val tripPostList = MutableLiveData<TripPost>()
    val tripPostLiked = MutableLiveData<Int>()

    val tripPostImage = MutableLiveData<Uri?>()

    init {
        tripPostImage.value = null
    }

    val myRequestState = MutableLiveData<Boolean>()


    // 오늘 날짜
    val currentTime: Long = System.currentTimeMillis()
    val dataFormat = SimpleDateFormat("yyyyMMdd")
    val today = dataFormat.format(currentTime).toInt()

    private val _tripPost = MutableLiveData<TripPost?>()
    val tripPost: LiveData<TripPost?>
        get() = _tripPost

    // 오늘 날짜 기준으로 참여/지난 동행글 구분하여 데이터 추출
    fun getAllTripPostData(userNickname: String) {
        val tripPostInfoList = mutableListOf<DocumentSnapshot>()
        val resultInProgressList = mutableListOf<TripPost>()
        val resultPassList = mutableListOf<TripPost>()

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val currentTripPostSnapshot =
                async { tripPostRepository.getAllDocumentData(userNickname) }
            tripPostInfoList.addAll(currentTripPostSnapshot.await().documents)

            for (document in tripPostInfoList) {
                val data = document.toObject(TripPost::class.java)
                var endDate = data?.tripPostDate

                // 참여중인 동행글
                if (today <= endDate!![1].toInt()) {
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

    // 좋아요한 게시글만 가져오기
    fun getTripPostLikedData(userEmail: String) {
        val tripPostInfoList = mutableListOf<DocumentSnapshot>()
        val resultLikedList = mutableListOf<TripPost>()

        val scope = CoroutineScope(Dispatchers.Default)

        scope.launch {
            val currentTripPostSnapshot =
                async { tripPostRepository.getTripPostLikedData(userEmail) }
            tripPostInfoList.addAll(currentTripPostSnapshot.await().documents)

            for (document in tripPostInfoList) {
                val data = document.toObject(TripPost::class.java)

                data!!.tripPostDocumentId = document.id

                resultLikedList.add(data)
            }

            withContext(Dispatchers.Main) {
                tripPostLikedList.value = resultLikedList
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
            val currentTripPostSnapshot =
                async { tripPostRepository.getSelectDocumentData(documentId) }
            val data = currentTripPostSnapshot.await().toObject(TripPost::class.java)

            result = data!!.tripPostLiked!!.size

            if (data != null) {
                resultData = data
                resultData.tripPostDocumentId = documentId
            }

            withContext(Dispatchers.Main) {
                tripPostList.value = resultData
                tripPostLiked.value = result
            }

            scope.cancel()
        }
    }

    // 동행글 이미지 가져오기
    fun getTripPostImage(tripPostImagePath: String) {
        try {
            val imageUri = runBlocking { tripPostRepository.getTripPostImage(tripPostImagePath) }

            if (imageUri != null) {
                tripPostImage.value = imageUri
            }
            else{
                tripPostImage.value = null
            }
        } catch (e: Exception) {

        }



    }

    fun getTargetUserTripPost(tripPostDocumentId: String) {
        val tripPostDocumentSnapshot =
            runBlocking {
                tripPostRepository.getTargetUserTripPost(tripPostDocumentId)
            }

        if (tripPostDocumentSnapshot != null) {
            val currentTripPost = tripPostDocumentSnapshot.toObject(TripPost::class.java)

            //현재 날짜 구하기
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val date: Long = currentDate.format(formatter).toLong()

            if (currentTripPost != null) {
                if (currentTripPost.tripPostDate!![0].toLong() > date) {
                    _tripPost.value = currentTripPost!!
                } else {
                    _tripPost.value = null
                }
            }
        }
    }

    //상세화면에서 해당 게시글의 나의 동행 요청상태가 대기중일 경우를 판단하는 메서드
    fun checkMyAccompanyRequestState(postId: String, myEmail: String) {
        val reviewSnapshot = mutableListOf<DocumentSnapshot>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //요청 데이터 쿼리문의 결과 스냅샷을 가져오기
            val snapshot = async { tripPostRepository.findRequestData(postId, myEmail) }
            reviewSnapshot.addAll(snapshot.await().documents)

            var forState: Boolean = true

            //쿼리문에 대한 스냅샷 데이터를 하나씩 가져와서 객체화
            runBlocking {
                for (docSnapshot in reviewSnapshot) {
                    val reviewObj = docSnapshot.toObject<TripRequest>()
                    //만약 가져온 데이터가 대기중이면 종료
                    if (reviewObj!!.tripRequestState == "대기중") {
                        withContext(Dispatchers.Main) {
                            myRequestState.value = true
                            scope.cancel()
                        }
                        forState = false
                        break
                    }
                }
            }
            if (forState == true) {
                withContext(Dispatchers.Main) {
                    myRequestState.value = false
                }
            }
            scope.cancel()
        }
    }


}