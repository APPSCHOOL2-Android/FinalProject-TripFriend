package com.test.tripfriend.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tripfriend.dataclassmodel.TripReview
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.TripReviewRepository
import kotlinx.coroutines.runBlocking

class TripReviewViewModel : ViewModel() {

    val tripReviewRepository = TripReviewRepository()

    private val _userReviewList = MutableLiveData<List<TripReview>>()
    val userReviewList : LiveData<List<TripReview>>
        get() = _userReviewList

    private val _reviewWriter = MutableLiveData<String>()
    val reviewWriter : LiveData<String>
        get() = _reviewWriter

    fun getTargetUserReviews(targetUserEmail:String){
        val currentUserReviewSnapshot =
            runBlocking {
                tripReviewRepository.getTargetUserReviews(targetUserEmail)
            }

        if(currentUserReviewSnapshot != null){
            val currentUserReviews = currentUserReviewSnapshot.toObjects(TripReview::class.java)
            _userReviewList.value = currentUserReviews
        }
    }

    fun getReviewWriterNickname(reviewWriterEmail : String){
        val reviewWriterSnapshot =
            runBlocking {
                tripReviewRepository.getReviewWriterNickname(reviewWriterEmail)
            }

        if(reviewWriterSnapshot != null){
            val reviewWriterUser = reviewWriterSnapshot.toObjects(User::class.java)
            reviewWriterUser.forEach {  user ->
                _reviewWriter.value = user.userNickname
            }
        }
    }
}