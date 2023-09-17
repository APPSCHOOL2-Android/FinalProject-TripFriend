package com.test.tripfriend.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.test.tripfriend.dataclassmodel.PersonalChatInfo
import com.test.tripfriend.dataclassmodel.PersonalChatRoom2
import com.test.tripfriend.dataclassmodel.PersonalChatting2
import com.test.tripfriend.repository.PersonalChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PersonalChatViewModel : ViewModel() {
    var chatInfoData = MutableLiveData<MutableList<PersonalChatInfo>>()


    //통신을 위한 레포지토리 객체
    val personalChatRepository = PersonalChatRepository()

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

                    //최근 채팅방 정보를 동기처리로 저장
                    val lastChatSnapshot =
                        async { personalChatRepository.getLastChat(roomIdList[idx]) }
                    lastChatInfo.addAll(lastChatSnapshot.await().documents)

                    for (document in lastChatInfo) {
                        val chatData = document.toObject(PersonalChatting2::class.java)
                        data.lastChatDate = chatData?.personalChatSendDateAndTime
                        data.lastChatContent = chatData?.personalChatContent
                    }

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
}