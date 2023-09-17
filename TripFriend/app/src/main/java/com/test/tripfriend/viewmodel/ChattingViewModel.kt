package com.test.tripfriend.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.test.tripfriend.dataclassmodel.PersonalChatRoom2
import com.test.tripfriend.dataclassmodel.PersonalChatting
import com.test.tripfriend.dataclassmodel.PersonalChatting2
import com.test.tripfriend.repository.PersonalChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChattingViewModel : ViewModel() {
    //통신을 위한 레포지토리 객체
    val personalChatRepository = PersonalChatRepository()

    val chattingList = MutableLiveData<MutableList<PersonalChatting2>>()

    fun chattingChangeListener(roomId: String) {
        personalChatRepository.getChatting(roomId) { snapshot, firebaseFirestoreException ->
            val dataList = mutableListOf<PersonalChatting2>()
            //오류처리
            if (firebaseFirestoreException != null) {
                return@getChatting
            }

            //데이터 가져와서 추가
            runBlocking {
                for (document in snapshot!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        // 새로운 문서가 추가되었을 때 할 작업을 여기에 작성하세요.
                        val data = document.document.toObject(PersonalChatting2::class.java)
                        dataList.add(data)

                    }
                }
            }
            chattingList.value = dataList
        }
    }

    //채팅방에 속해있는 멤버 정보를 이메일을 토대로 가져온다,
    fun fetchMember(roomId: String) {
        val memberEmails = mutableListOf<DocumentSnapshot>()
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //멤버의 이메일 가져오기
            val memberEmail = async { personalChatRepository.getchatMember(roomId) }
            val memberEmailObj = memberEmail.await()?.toObject(PersonalChatRoom2::class.java)


//            Log.d("testt","${memberEmailObj?.personalChatRequesterEmail}")
//            Log.d("testt","${memberEmailObj?.personalChatPostWriterEmail}")

        }

    }

}