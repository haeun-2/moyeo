package com.d108.moyeo.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.d108.moyeo.MainActivity
import com.d108.moyeo.R
import com.d108.moyeo.domain.usecase.fcm.RegisterFcmTokenUseCase
import com.d108.moyeo.presentation.navigation.AppScreen
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "MoyeoFCM"

@AndroidEntryPoint
class MoyeoFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registerFcmTokenUseCase: RegisterFcmTokenUseCase

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "새로운 FCM 토큰 발급: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            registerFcmTokenUseCase(token)
                .onSuccess {
                    Log.d(TAG, "FCM 토큰 서버 등록 성공")
                }
                .onFailure { error ->
                    Log.e(TAG, "FCM 토큰 서버 등록 실패", error)
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "======== FCM 메시지 수신됨 ========")
        Log.d(TAG, "From: ${remoteMessage.from}")
        Log.d(TAG, "Data: ${remoteMessage.data}")
        Log.d(TAG, "Notification: ${remoteMessage.notification}")

        // 우선순위: data 페이로드 > notification 페이로드 > 기본값
        val title = remoteMessage.data["title"]
            ?: remoteMessage.notification?.title
            ?: "모여 알림"

        val body = remoteMessage.data["body"]
            ?: remoteMessage.notification?.body
            ?: "새로운 메시지가 도착했습니다."

        val type = remoteMessage.data["type"]
        val boxId = remoteMessage.data["boxId"]

        // TODO: 나중에 type에 따라 다른 딥링크를 생성하도록 확장할 수 있습니다.
        // when (type) {
        //     "INVITE" -> ...
        //     "CHAT" -> ...
        //     else -> sendNotification(title, body)
        // }

        sendNotification(title, body)
    }

    private fun sendNotification(title: String, messageBody: String) {
        Log.d(TAG, "알림 생성 시도: $title - $messageBody")

        val channelId = "moyeo_default_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 각 알림을 식별할 고유 ID를 먼저 생성합니다.
        val notificationId = Random.nextInt()

        // 알림을 탭했을 때 홈 화면으로 이동하는 인텐트 생성
        val intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("moyeo://${AppScreen.Home.route}")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId, // requestCode를 고유하게 만들어 각 알림이 덮어쓰이지 않도록 함
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 채널 생성 (Android O 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "모여 기본 알림",
                NotificationManager.IMPORTANCE_HIGH // 중요도를 HIGH로 설정
            ).apply {
                description = "모여 앱의 모든 기본 알림을 위한 채널입니다."
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 빌더 생성
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true) // 탭하면 자동으로 사라지도록 설정
            .setPriority(NotificationCompat.PRIORITY_HIGH) // 우선순위를 HIGH로 설정
            .setDefaults(NotificationCompat.DEFAULT_ALL) // 소리, 진동 등 기본 설정
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // 잠금화면에서 내용 표시
            .setContentIntent(pendingIntent) // 알림 탭 시 실행할 작업
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // 기본 알림음
            .setVibrate(longArrayOf(0, 300, 200, 300)) // 진동 패턴
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody)) // 긴 텍스트 스타일

        notificationManager.notify(notificationId, notificationBuilder.build())
        Log.d(TAG, "알림 전송(notify) 호출 완료: $title")
    }
}

