package com.d108.moyeo.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

/**
 * 주어진 문자열(content)을 QR 코드 ImageBitmap으로 변환
 * @param content QR 코드로 만들 문자열 (예: 서버에서 받은 토큰)
 * @param size QR 코드 이미지의 가로/세로 크기 (픽셀 단위)
 * @return 변환에 성공하면 ImageBitmap 객체를, 실패하면 null을 반환
 */
fun generateQRCodeBitmap(content: String, size: Int = 512): ImageBitmap? {
    return try {
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}