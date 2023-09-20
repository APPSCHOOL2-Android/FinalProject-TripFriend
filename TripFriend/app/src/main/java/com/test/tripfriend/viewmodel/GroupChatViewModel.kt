package com.test.tripfriend.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.test.tripfriend.dataclassmodel.GroupChatInfo
import com.test.tripfriend.dataclassmodel.GroupChatRoom
import com.test.tripfriend.dataclassmodel.GroupChatting
import com.test.tripfriend.dataclassmodel.PostInfo
import com.test.tripfriend.dataclassmodel.TripPost
import com.test.tripfriend.repository.GroupChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class GroupChatViewModel : ViewModel() {
    //통신을 위한 레포지토리 객체
    val groupChatRepository = GroupChatRepository()
    //단체 채팅방 정보를 담고있는 라이브데이터
    val groupChatRoomInfo = MutableLiveData<MutableList<GroupChatInfo>>()

    val changeString = MutableLiveData<String>()

    private val _groupChatDocumentId = MutableLiveData<String>()
    val groupChatDocumentId : LiveData<String>
        get() = _groupChatDocumentId

    ////나와 관련된 단체 채팅방을 모두 불러와서 채팅방을 띄우는데 필요한 정보를 수집
    fun fetchGroupChatRoomInfo(myNickname: String) {
        val rooms = mutableListOf<DocumentSnapshot>()
        val scope = CoroutineScope(Dispatchers.Default)
        val groupChatInfo = mutableListOf<GroupChatInfo>()
        scope.launch {

            //내 이메일과 관련된 그룹 채팅방 정보를 담겨있는 스냅샷 가져오기
            val roomSnapshot = async { groupChatRepository.getMyGroupChatRoom(myNickname) }

            //스냅샷 저장
            rooms.addAll(roomSnapshot.await().documents)

            //채팅방 정보를 모두 탐색
            for (document in rooms) {
                val lastChatInfo = mutableListOf<DocumentSnapshot>()
                val groupChatRoomObj= GroupChatInfo()

                //채팅방의 아이디 저장(입장할때 용이하기 위함)
                groupChatRoomObj.roomId=document.id

                //읽어온 채팅방 데이터 하나
                val roomObj = document.toObject(GroupChatRoom::class.java)

                //채팅방에 해당하는 동행글 제목을 가져오기 위한 작업
                val postInfoSnapshot =
                    async { groupChatRepository.getRoomInfoFromPost(roomObj?.groupChatTripPostId!!) }
                val postInfo = postInfoSnapshot.await().toObject(PostInfo::class.java)
                val postId=postInfoSnapshot.await().id
                //동행글 타이틀 저장
                groupChatRoomObj.tripPostTitle=postInfo?.tripPostTitle
                //동행글 id저장
                groupChatRoomObj.tripPostId=postId
                //참여한 인원수 저장
                groupChatRoomObj.memberCount=postInfo?.tripPostMemberList?.size

                //최근 채팅방 정보를 동기처리로 저장
                val lastChatSnapshot =
                    async { groupChatRepository.getLastChat(document.id) }
                lastChatInfo.addAll(lastChatSnapshot.await().documents)

                for (lastChat in lastChatInfo) {
                    val chatData = lastChat.toObject(GroupChatting::class.java)
                    groupChatRoomObj.lastChatDate = chatData?.groupChatSendDateAndTime
                    groupChatRoomObj.lastChatContent = chatData?.groupChatContent
                    groupChatInfo.add(groupChatRoomObj)
                    Log.d("이거맞나","${groupChatRoomObj.roomId}")
                    Log.d("이거맞나","${groupChatRoomObj.memberCount}")
                    Log.d("이거맞나","${groupChatRoomObj.tripPostId}")
                    Log.d("이거맞나","${groupChatRoomObj.lastChatContent}")
                    Log.d("이거맞나","${groupChatRoomObj.lastChatDate}")
                    Log.d("이거맞나","${groupChatRoomObj.tripPostTitle}")

                }
            }
            //메인 쓰레드에서 라이브 데이터 저장
            withContext(Dispatchers.Main) {
                groupChatRoomInfo.value = groupChatInfo
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
            val chatRoomRef = db.collection("GroupChatRoom").document(chatRoomDocumentId)
            val chatListenerRegistration = chatRoomRef
                .collection("GroupChatting")
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

    fun getGroupChatDocumentId(tripPostDocumentId: String){
        val groupChatDocSnapshot =
            runBlocking {
                groupChatRepository.getGroupChatDocumentId(tripPostDocumentId)
            }

        if(groupChatDocSnapshot != null) {
            groupChatDocSnapshot.documents.forEach { docSnapshot ->
                _groupChatDocumentId.value = docSnapshot.id
            }
        }
    }
}