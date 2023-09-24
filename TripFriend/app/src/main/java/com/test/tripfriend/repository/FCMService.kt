package com.test.tripfriend.repository

import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMService {
    @POST("fcm/send")
    suspend fun sendFCM(
        @Header("Content-Type") type:String,
        @Header("Authorization") key:String,
        @Body body: FCMRequest
    ): Response<Any>
}

// FCM 알림 요청 데이터 클래스
data class FCMRequest(val data: Map<String, String>, val to: String)

// 알림 내용 데이터 클래스
data class Notification(val title: String, val body: String)

// Retrofit을 초기화하는 함수
fun createFCMService(): FCMService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://fcm.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    return retrofit.create(FCMService::class.java)
}

// FCM 메시지 전송
suspend fun sendFCMMessage(fcmService: FCMService, title: String, body: String, to: String): Any?{
    val notification = Notification(title, body)
    val request = FCMRequest(
        data = mapOf(
            "title" to title,
            "body" to body
        ), to
    ) //token

    try{
        val response : Response<Any> = fcmService.sendFCM("application/json", "key=AAAA5S5mCz0:APA91bFw9XnXZt3q3l3O6wHIjipYumL_OrWW00O9JUU1hsyicqtMJQiKCR5DoSZwT4nLfMdZt6u4Fdw3-PdSXcfHxCj8zQADeIBxWfV7wyXnv-N4oClEdBD8tgNqUG2e4xA4ZAagfafz",request)

        if (response.isSuccessful) {
            Log.d("success", "FCM message sent successfully.")
            Log.d("mentor", response.body().toString())
            return response.body()
        } else {
            Log.d("failed", "Failed to send FCM message. Response code: ${response.code()}")
            return null
        }

    }catch (e: Exception){
        Log.e("Retrofit Error", e.message.toString())
        return null
    }
}
