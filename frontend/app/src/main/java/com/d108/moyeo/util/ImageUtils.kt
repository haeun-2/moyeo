package com.d108.moyeo.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap

/**
 * Base64로 인코딩된 문자열을 ImageBitmap으로 변환하는 유틸리티 함수.
 * @param base64String 서버로부터 받은 Base64 문자열.
 * @return 변환에 성공하면 ImageBitmap, 실패하면 null을 반환.
 */
fun base64ToImageBitmap(base64String: String?): ImageBitmap? {  // 서버에서 이미지가 null 일 수 있음
    return try {
        // 1. Base64 문자열을 원래의 바이트 배열로 디코딩
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // 2. 바이트 배열을 Bitmap 객체로 변환
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // 3. Compose의 Image 컴포저블이 사용할 수 있는 ImageBitmap으로 최종 변환
        bitmap.asImageBitmap()
    } catch (e: IllegalArgumentException) {
        // Base64 문자열 형식이 잘못되었을 경우 에러가 발생할 수 있습니다.
        e.printStackTrace()
        null
    }
}