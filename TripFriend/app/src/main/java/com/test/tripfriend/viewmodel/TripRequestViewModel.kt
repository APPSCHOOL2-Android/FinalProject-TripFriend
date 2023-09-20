package com.test.tripfriend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.tripfriend.dataclassmodel.TripRequest
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.repository.TripRequestRepository
import kotlinx.coroutines.runBlocking

class TripRequestViewModel : ViewModel() {

    val tripRequestRepository = TripRequestRepository()

    //받은 요청 객체 리스트
    private val _tripRequest = MutableLiveData<List<TripRequest>>()
    val tripRequest : LiveData<List<TripRequest>>
        get() = _tripRequest

    //받은 요청 문서 Id값
    private val _tripRequestDocumentId = MutableLiveData<List<String>>()
    val tripRequestDocumentId : LiveData<List<String>>
        get() = _tripRequestDocumentId

    //보낸 요청 객체 리스트
    private val _sentTripRequest = MutableLiveData<List<TripRequest>>()
    val sentTripRequest : LiveData<List<TripRequest>>
        get() = _sentTripRequest

    //보낸 요청 문서 Id값
    private val _sentTripRequestDocumentId = MutableLiveData<List<String>>()
    val sentTripRequestDocumentId : LiveData<List<String>>
        get() = _sentTripRequestDocumentId

    fun getAllReceivedTripRequest(tripRequestReceiverEmail : String){

        val tripRequestQuerySnapshot = runBlocking {
            tripRequestRepository.getAllReceivedTripRequest(tripRequestReceiverEmail)
        }

        if(tripRequestQuerySnapshot != null){
            val documentIdList = mutableListOf<String>()
            val tripRequestList = mutableListOf<TripRequest>()

            tripRequestQuerySnapshot.documents.forEach { documentSnapshot ->
                documentIdList.add(documentSnapshot.id)
            }
            _tripRequestDocumentId.value = documentIdList

            val tripRequests = tripRequestQuerySnapshot.toObjects(TripRequest::class.java)
            tripRequests.forEach { request ->
                tripRequestList.add(request)
            }
            _tripRequest.value = tripRequestList
        }
    }

    fun getAllSentTripRequest(tripRequestWriterEmail : String){

        val sentTripRequestQuerySnapshot = runBlocking {
            tripRequestRepository.getAllSentTripRequest(tripRequestWriterEmail)
        }

        if(sentTripRequestQuerySnapshot != null){
            val sentDocIdList = mutableListOf<String>()
            val sentTripRequestList = mutableListOf<TripRequest>()

            sentTripRequestQuerySnapshot.documents.forEach { sentDocumentSnapshot ->
                sentDocIdList.add(sentDocumentSnapshot.id)
            }
            _sentTripRequestDocumentId.value = sentDocIdList

            val sentTripRequests = sentTripRequestQuerySnapshot.toObjects(TripRequest::class.java)
            sentTripRequests.forEach { sentTripRequest ->
                sentTripRequestList.add(sentTripRequest)
            }
            _sentTripRequest.value = sentTripRequestList
        }
    }
}