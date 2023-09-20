package com.test.tripfriend.repository

import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.test.tripfriend.dataclassmodel.User
import com.test.tripfriend.dataclassmodel.UserLogin
import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

class UserRepository {
    companion object {
        fun addUser(userClass: User, callback: (Task<DocumentReference>) -> Unit) {
            // Firestore 초기화
            val db = FirebaseFirestore.getInstance()

            // 사용자 정보 입력
            val user = HashMap<String, Any>()
            user["userAuthentication"] = userClass.userAuthentication
            user["userEmail"] = userClass.userEmail
            user["userPw"] = userClass.userPw
            user["userName"] = userClass.userName
            user["userNickname"] = userClass.userNickname
            user["userPhoneNum"] = userClass.userPhoneNum
            user["userMBTI"] = userClass.userMBTI
            user["userProfilePath"] = userClass.userProfilePath
            user["userFriendSpeed"] = userClass.userFriendSpeed
            user["userTripScore"] = userClass.userTripScore
            user["userTripCount"] = userClass.userTripCount
            user["userChatNotification"] = userClass.userChatNotification
            user["userPushNotification"] = userClass.userPushNotification

            // Firestore 컬렉션에 추가
            db.collection("User")
                .add(user)
                .addOnCompleteListener(callback)
        }

        fun getAllUser(callback: (Task<QuerySnapshot>) -> Unit) {
            val db = FirebaseFirestore.getInstance()
            db.collection("User")
                .get()
                .addOnCompleteListener(callback)
        }
        fun saveUserInfo(sharedPreferences: SharedPreferences, userClass: User, autoLogin:Boolean){
            val editor = sharedPreferences.edit()

            editor.putString("userAuthentication", userClass.userAuthentication)
            editor.putString("userEmail", userClass.userEmail)
            editor.putString("userPw", userClass.userPw)
            editor.putString("userNickname", userClass.userNickname)
            editor.putString("userName", userClass.userName)
            editor.putString("userPhoneNum", userClass.userPhoneNum)
            editor.putString("userMBTI", userClass.userMBTI)
            editor.putString("userProfilePath", userClass.userProfilePath)
            editor.putFloat("userFriendSpeed", userClass.userFriendSpeed.toFloat())
            editor.putFloat("userTripScore", userClass.userTripScore.toFloat())
            editor.putLong("userTripCount", userClass.userTripCount.toLong())
            editor.putBoolean("userChatNotification", userClass.userChatNotification)
            editor.putBoolean("userPushNotification", userClass.userPushNotification)
            editor.putBoolean("autoLogin",autoLogin)

            editor.apply()
        }

        fun saveNoneUserInfo(sharedPreferences: SharedPreferences){
            val editor = sharedPreferences.edit()

            editor.putString("userAuthentication", "NoneUserAuthentication")
            editor.putString("userEmail", "NoneUserEmail")
            editor.putString("userPw", "NoneUSerPw")
            editor.putString("userNickname", "NoneUserNickname")
            editor.putString("userName", "NoneUserName")
            editor.putString("userPhoneNum", "NoneUserPhoneNum")
            editor.putString("userMBTI", "NoneUserMBTI")
            editor.putString("userProfilePath", "NoneUserProfilePath")
            editor.putFloat("userFriendSpeed", 10f)
            editor.putFloat("userTripScore", 0f)
            editor.putLong("userTripCount", 0L)
            editor.putBoolean("userChatNotification", false)
            editor.putBoolean("userPushNotification", false)
            editor.putBoolean("autoLogin",false)

            editor.apply()
        }

        fun checkUserInfo(sharedPreferences: SharedPreferences): Boolean{

            val userAuthentication = sharedPreferences.getString("userAuthentication", null)
            val userEmail = sharedPreferences.getString("userEmail", null)
            val userPw = sharedPreferences.getString("userPw", null)
            val userNickname = sharedPreferences.getString("userNickname", null)
            val userName = sharedPreferences.getString("userName", null)
            val userMBTI = sharedPreferences.getString("userMBTI", null)
            val userProfilePath = sharedPreferences.getString("userProfilePath", null)
            val userFriendSpeed = sharedPreferences.getFloat("userFriendSpeed", 0.0f)
            val userPhoneNum = sharedPreferences.getString("userPhoneNum", null)
            val userTripScore = sharedPreferences.getFloat("userTripScore", -1f)
            val userTripCount = sharedPreferences.getLong("userTripCount", -1L)
            val userChatNotification = sharedPreferences.getBoolean("userChatNotification", true)
            val userPushNotification = sharedPreferences.getBoolean("userPushNotification", true)
            val autoLogin = sharedPreferences.getBoolean("autoLogin", false)

            return !(userAuthentication == null || userEmail == null || userPw == null ||
                    userNickname == null || userName == null || userMBTI == null ||
                    userProfilePath == null || userPhoneNum == null || userTripScore == -1f ||
                    userTripCount == -1L)
        }

        //로그인 정보가 저장됐을 경우만
        fun getUserInfo(sharedPreferences: SharedPreferences) : UserLogin{
            val userAuthentication = sharedPreferences.getString("userAuthentication", null)
            val userEmail = sharedPreferences.getString("userEmail", null)
            val userPw = sharedPreferences.getString("userPw", null)
            val userNickname = sharedPreferences.getString("userNickname", null)
            val userName = sharedPreferences.getString("userName", null)
            val userMBTI = sharedPreferences.getString("userMBTI", null)
            val userProfilePath = sharedPreferences.getString("userProfilePath", null)
            val userFriendSpeed = sharedPreferences.getFloat("userFriendSpeed", 0.0f)
            val userPhoneNum = sharedPreferences.getString("userPhoneNum", null)
            val userTripScore = sharedPreferences.getFloat("userTripScore", -1f)
            val userTripCount = sharedPreferences.getLong("userTripCount", -1L)
            val userChatNotification = sharedPreferences.getBoolean("userChatNotification", true)
            val userPushNotification = sharedPreferences.getBoolean("userPushNotification", true)
            val autoLogin = sharedPreferences.getBoolean("autoLogin", false)

            val userClass = UserLogin(
                userAuthentication!!,
                userEmail!!,
                userPw!! ,
                userNickname!! ,
                userName!!,
                userPhoneNum!! ,
                userMBTI!!,
                userProfilePath = userProfilePath!!,
                userFriendSpeed = userFriendSpeed.toDouble()!!,
                userTripScore = userTripScore!!.toDouble(),
                userTripCount = userTripCount!!.toInt(),
                userChatNotification = userChatNotification!!,
                userPushNotification = userPushNotification!!,
                autoLogin
            )

            return userClass
        }

        fun resetUserInfo(sharedPreferences: SharedPreferences){
            //sharedreferences 값 삭제
            val editor = sharedPreferences.edit()

            editor.remove("userAuthentication")
            editor.remove("userEmail")
            editor.remove("userPw")
            editor.remove("userNickname")
            editor.remove("userName")
            editor.remove("userPhoneNum")
            editor.remove("userMBTI")
            editor.remove("userProfilePath")
            editor.remove("userFriendSpeed")
            editor.remove("userTripScore")
            editor.remove("userTripCount")
            editor.remove("userChatNotification")
            editor.remove("userPushNotification")
            editor.remove("autoLogin")

            editor.apply()
        }
    }

    //유저 정보 가져오는 함수
    suspend fun getTargetUserData(targetUserEmail:String) : QuerySnapshot{
        //파이어스토어 객체 얻기
        val firestore = Firebase.firestore

        //인자로 받은 이메일 값과 일치하는 필드
        return firestore.collection("User")
            .whereEqualTo("userEmail", targetUserEmail)
            .get().await()
    }

    //유저 프로필 이미지 url 가져오는 함수
    suspend fun getTargetUserProfileImage(targetUserProfileImagePath : String) : Uri {
        val storage = Firebase.storage

        //인자로 전달된 profileImagePath의 경로 형태 확인 필
        val fileRef = storage.reference.child(targetUserProfileImagePath)
        return fileRef.downloadUrl.await()
    }

    //푸시 알람 설정 여부 업데이트하는 함수
    suspend fun updateTargetUserPushNotification(documentId : String, pushNotificationState : Boolean){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .update("userPushNotification",pushNotificationState).await()
    }

    //채팅 알람 설정 여부 업데이트하는 함수
    suspend fun updateTargetUserChatNotification(documentId : String, chatNotificationState : Boolean){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .update("userChatNotification",chatNotificationState).await()
    }

    //유저 정보 삭제하는 함수
    fun deleteTargetUserData(documentId: String){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .delete()
    }

    //유저 정보 업데이트 하는 함수
    suspend fun updateTargetUserInfo(
        documentId: String, userProfilePath : String, userNickname : String, userMBTI : String, userPw : String
    ){
        val firestore = Firebase.firestore

        firestore.collection("User")
            .document(documentId)
            .update(
                mapOf(
                    "userProfilePath" to userProfilePath,
                    "userNickname" to userNickname,
                    "userMBTI" to userMBTI,
                    "userPw" to userPw
                )
            ).await()
    }

    fun updateTargetUserProfile(filePath : String, uploadUri: Uri){
        val storage = Firebase.storage

        val imageRef = storage.reference.child(filePath)
        imageRef.putFile(uploadUri)
    }
}
