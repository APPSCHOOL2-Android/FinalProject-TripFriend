package com.test.tripfriend.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.test.tripfriend.dataclassmodel.GroupChatting
import com.test.tripfriend.dataclassmodel.PersonalChatRoom2
import com.test.tripfriend.dataclassmodel.PersonalChatting
import com.test.tripfriend.dataclassmodel.PersonalChatting2
import com.test.tripfriend.dataclassmodel.PostInfo
import com.test.tripfriend.dataclassmodel.UserInfo
import com.test.tripfriend.repository.GroupChatRepository
import com.test.tripfriend.repository.PersonalChatRepository
import com.test.tripfriend.repository.TripPostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChattingViewModel : ViewModel() {
    //통신을 위한 레포지토리 객체
    val personalChatRepository = PersonalChatRepository()
    val groupChatRepository = GroupChatRepository()
    val tripPostRepository= TripPostRepository()

    val chattingList = MutableLiveData<MutableList<PersonalChatting2>>()
    val groupChattingList = MutableLiveData<MutableList<GroupChatting>>()
    val groupUserInfoMapList = MutableLiveData<MutableList<MutableMap<String,String>>>()
    val myProfile = MutableLiveData<Uri?>()

    //문의방 데이터를 가져오고 db에 변동이 발생하면 추가하는 메서드
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

    //그룹방 데이터를 가져오고 db에 변동이 발생하면 추가하는 메서드
    fun groupChattingChangeListener(roomId: String) {
        groupChatRepository.getChatting(roomId) { snapshot, firebaseFirestoreException ->
            val dataList = mutableListOf<GroupChatting>()
            //오류처리
            if (firebaseFirestoreException != null) {
                return@getChatting
            }

            //데이터 가져와서 추가
            runBlocking {
                for (document in snapshot!!.documentChanges) {
                    if (document.type == DocumentChange.Type.ADDED) {
                        // 새로운 문서가 추가되었을 때 할 작업을 여기에 작성하세요.
                        val data = document.document.toObject(GroupChatting::class.java)
                        dataList.add(data)
                    }
                }
            }
            groupChattingList.value = dataList
        }
    }

    //그룹 채팅방에 참여한 사람들의 정보를 가져오는 메서드
    fun getUserDataInGroupChat(postId: String) {
        val scope = CoroutineScope(Dispatchers.Default)


        scope.launch {
            val userEmailMap = mutableMapOf<String, String>()
            val userImageMap = mutableMapOf<String, String>()
            val result= mutableListOf<MutableMap<String,String>>()
            val postSnapshot = async { groupChatRepository.getRoomInfoFromPost(postId) }

            val postInfo = postSnapshot.await().toObject(PostInfo::class.java)

            //얻어온 멤버의 닉네임 리스트
            val member = postInfo?.tripPostMemberList

            if (member != null) {
                for (name in member) {
                    val users = mutableListOf<DocumentSnapshot>()
                    //이름에 해당하는 유저 정보가 담긴 스냅샷 가져오기
                    val userSnapshot = async { groupChatRepository.getUserInfoByNickname(name) }
                    //스냅샷 저장
                    users.addAll(userSnapshot.await().documents)

                    //스탭샷 내부 문서 전부 탑색(어차피 한번 실시)
                    for (user in users) {
                        val data=user.toObject(UserInfo::class.java)
                        val email=data?.userEmail
                        Log.d("zzzz","${email}")
                        val image=data?.userProfilePath
                        val imageUri:Uri?
                        //이미지uri가져오는 작업
                        if(image==""||image==null||image=="null"){
                            imageUri=null
                        }else{
                            runBlocking {
                                val uri=personalChatRepository.getUserProfileImage(image)
                                imageUri=uri
                            }
                        }
                        if (email != null && image != null) {
                            userEmailMap.put(email,name)
                            userImageMap.put(email,imageUri.toString())
                        }
                    }

                }
                result.add(userEmailMap)
                result.add(userImageMap)

            }
            //메인 쓰레드에서 라이브데이터 설정
            withContext(Dispatchers.Main) {
                groupUserInfoMapList.value = result
            }
            //코루틴 종료
            scope.cancel()
        }
    }

    fun getMyImageUri(myImagePath:String){
        //이미지uri가져오는 작업
        if(myImagePath==""||myImagePath==null||myImagePath=="null"){
            myProfile.value=null
        }else{
            runBlocking {
                val uri=personalChatRepository.getUserProfileImage(myImagePath)
                myProfile.value=uri
            }
        }
    }

    //개인 채팅방을 나가면 해당 채팅방을 삭제한다.
    fun removePersonalChatRoom(roomId:String){
        personalChatRepository.removePersonalChatRoomById(roomId)
    }

    //그룹 채팅방에서 나가기를 클릭하면 멤버 삭제(채팅방, 동핼글)
    fun outMemberFromChatRoom(nickName:String,roomId:String,tripId:String){
        groupChatRepository.deleteGroupChatMemberNickname(nickName,roomId)
        tripPostRepository.deleteTripMemberNickname(nickName,tripId)
    }


}