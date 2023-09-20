package com.test.tripfriend.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.test.tripfriend.dataclassmodel.PersonalChatInfo
import com.test.tripfriend.dataclassmodel.PersonalChatRoom2
import com.test.tripfriend.dataclassmodel.PersonalChatting2
import com.test.tripfriend.repository.PersonalChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class PersonalChatViewModel : ViewModel() {
    var chatInfoData = MutableLiveData<MutableList<PersonalChatInfo>>()


    //통신을 위한 레포지토리 객체
    val personalChatRepository = PersonalChatRepository()

    //채팅방의 변화가 감지되는지 감시하기 위한 라이브데이터
    val changeString = MutableLiveData<String>()

    val imageUriList = MutableLiveData<MutableList<Uri?>>()

    //나와 관련된 채팅방을 모두 불러와서 채팅방을 띄우는데 필요한 정보를 수집
    fun fetchChatRoomInfo(myEmail: String) {
        val rooms = mutableListOf<DocumentSnapshot>()
        val roomInfo = mutableListOf<DocumentSnapshot>()
        val personalChatInfo = mutableListOf<PersonalChatInfo>()
        val roomIdList = mutableListOf<String>()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            //내 이메일과 관련된 모든 채팅방 정보를 담겨있는 스냅샷 가져오기
            val roomSnapshot = async { personalChatRepository.getMyPersonalChatRoom(myEmail) }

            //스냅샷 저장
            rooms.addAll(roomSnapshot.await().documents)

            //채팅방 정보를 모두 탐색
            for (document in rooms) {
                //채팅
                roomIdList.add(document.id)
                val data = document.toObject(PersonalChatRoom2::class.java)

                //채팅방 목록 정보를 가져오기 위한 스냅샷 가져와서 저장(내 상대의 정보를 저장해야하기에 분기문)
                val chatRoomSnapshot = async {
                    personalChatRepository.getPersonalChatInfo(
                        //상대방의 정보를 가져와서 목록 아이템에 띄워야 하므로 분기문으로 캐스팅
                        if (data?.personalChatRequesterEmail.toString() == myEmail) {
                            data?.personalChatPostWriterEmail ?: "null"
                        } else {
                            data?.personalChatRequesterEmail ?: "null"
                        }
                    )
                }
                //가져온 채팅방 목록 스냅샷 저장
                roomInfo.addAll(chatRoomSnapshot.await().documents)
            }

            //채팅방 목록의 데이터를 탐색하고 해당 채팅방id저장
            for (idx in 0 until roomInfo.size) {
                val lastChatInfo = mutableListOf<DocumentSnapshot>()
                val data = roomInfo[idx].toObject(PersonalChatInfo::class.java)
                if (data != null) {
                    data.documentId = roomIdList[idx]

                    //이미지uri가져오는 작업
                    if(data.userProfilePath==""||data.userProfilePath==null||data.userProfilePath=="null"){
                        data.imageUri=null
                    }else{
                        runBlocking {
                            val uri=personalChatRepository.getUserProfileImage(data.userProfilePath!!)
                            data.imageUri=uri
                        }
                    }

                    //최근 채팅방 정보를 동기처리로 저장
                    val lastChatSnapshot =
                        async { personalChatRepository.getLastChat(roomIdList[idx]) }
                    lastChatInfo.addAll(lastChatSnapshot.await().documents)

                    for (document in lastChatInfo) {
                        val chatData = document.toObject(PersonalChatting2::class.java)
                        data.lastChatDate = chatData?.personalChatSendDateAndTime
                        data.lastChatContent = chatData?.personalChatContent
                    }
                    Log.d("gogo","${data.imageUri}")
                    personalChatInfo.add(data)
                }
            }

            //메인 쓰레드에서 라이브데이터 설정
            withContext(Dispatchers.Main) {
                chatInfoData.value = personalChatInfo
            }

            //코루틴 종료
            scope.cancel()
        }
    }

    //채팅에 변경사항이 있으면 호출되는 함수
    fun fetchChangeInfo(roomId: String) {
        val db = FirebaseFirestore.getInstance()
        val chatRoomDocumentId = roomId // PersonalChatRoom 문서의 ID
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            // PersonalChatting 하위 컬렉션에 대한 리스너 설정
            val chatRoomRef = db.collection("PersonalChatRoom").document(chatRoomDocumentId)
            val chatListenerRegistration = chatRoomRef
                .collection("PersonalChatting")
                .addSnapshotListener { querySnapshot, e ->
                    if (e != null) {
                        // 오류 처리
                        return@addSnapshotListener
                    }

                    // PersonalChatting 하위 컬렉션에 대한 변경 사항 처리
                    for (documentChange in querySnapshot!!.documentChanges) {
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                // PersonalChatting 컬렉션 내의 문서가 추가됐을 때 처리
                                val chatDocument = documentChange.document
                                val chatId = chatDocument.id
                                changeString.value="추가됨"

                                // 여기에서 새로운 채팅 메시지를 처리
                            }
                            DocumentChange.Type.MODIFIED -> {
                                // PersonalChatting 컬렉션 내의 문서가 수정됐을 때 처리
                                val chatDocument = documentChange.document
                                val chatId = chatDocument.id
                                changeString.value="수정됨"
                                // 여기에서 수정된 채팅 메시지를 처리
                            }
                            DocumentChange.Type.REMOVED -> {
                                // PersonalChatting 컬렉션 내의 문서가 삭제됐을 때 처리
                                val chatDocument = documentChange.document
                                val chatId = chatDocument.id
                                changeString.value="삭제됨"
                                // 여기에서 삭제된 채팅 메시지를 처리
                            }
                        }
                    }
                }
        }
    }

    //개인 채팅방의 상대 이미지 uri를 가져오는 작업(아이템마다)
//    fun getImageUri(list:MutableList<PersonalChatInfo>){
//        val resultList= mutableListOf<Uri?>()
//        for (info in list){
//            //이미지 경로가 null이면 uri리스트에 null삽입
//            if(info.userProfilePath==""||info.userProfilePath==null||info.userProfilePath=="null"){
//                resultList.add(null)
//            }else{
//                runBlocking {
//                    val uri=personalChatRepository.getUserProfileImage(info.userProfilePath!!)
//                    resultList.add(uri)
//                }
//            }
//        }
//        imageUriList.value=resultList
//
//    }


}