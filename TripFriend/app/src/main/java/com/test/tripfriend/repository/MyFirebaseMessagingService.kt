package com.test.tripfriend.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.test.tripfriend.R
import com.test.tripfriend.ui.main.MainActivity
import kotlinx.coroutines.tasks.await
import java.util.UUID


class MyFirebaseMessagingService : FirebaseMessagingService() {

    val NOTIFICATION_CHANNEL1_ID = "NotificationChannel"
    val NOTIFICATION_CHANNEL1_Name = "NotificationChannel"

    //FCM 서버에 토큰이 등록되었을 때 호출됨
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("Token", "FCM token created: $token")
    }

    suspend fun getToken() : String{
        return FirebaseMessaging.getInstance().token.await()
    }

    //수신한 메시지 처리
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.data.isNotEmpty()){
            Log.d("message", message.data["title"].toString())
            Log.d("message", message.data["body"].toString())
            // 알림 표시
            sendNotification(message.data)
        }
    }

    fun createNotificationChannel(channelId:String, channelName:String, notificationBuilder : NotificationCompat.Builder){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = notificationManager.getNotificationChannel(channelId)

            if(channel==null){
                val newChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
                newChannel.enableVibration(true)
                //newChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                notificationManager.createNotificationChannel(newChannel)
            }
            val requestCode = UUID.randomUUID().hashCode()
            notificationManager.notify(requestCode, notificationBuilder.build())
        }
    }

    fun getNotificationBuilder(channelId: String) : NotificationCompat.Builder{
        //안드로이ㅣ드 8.0 이상이라면
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val builder = NotificationCompat.Builder(this, channelId)
            return builder
        }else{
            val builder = NotificationCompat.Builder(this)
            return builder
        }
    }

    fun sendNotification(messageData : Map<String, String>){

        val messageBody = messageData["body"]
        val messageTitle = messageData["title"]

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = UUID.randomUUID().hashCode()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            FLAG_MUTABLE
        )

        val notificationBuilder = getNotificationBuilder(NOTIFICATION_CHANNEL1_ID)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(messageBody)
            .setContentTitle(messageTitle)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.info_24px)
//            .setFullScreenIntent(pendingIntent, true)

        createNotificationChannel(NOTIFICATION_CHANNEL1_ID, NOTIFICATION_CHANNEL1_Name, notificationBuilder)
    }
}