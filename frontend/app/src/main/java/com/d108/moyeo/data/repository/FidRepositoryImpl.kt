package com.d108.moyeo.data.repository

import com.d108.moyeo.domain.repository.FidRepository
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class FidRepositoryImpl @Inject constructor() : FidRepository {
    override suspend fun getFid(): Result<String> = runCatching {
        // suspendCancellableCoroutine은 콜백 기반의 비동기 코드를 코루틴의 suspend 함수처럼 바꿔주는 함수
        suspendCancellableCoroutine { continuation ->
            FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 성공하면, 코루틴에게 결과값을 돌려주며 작업을 재개
                    continuation.resume(task.result)
                } else {
                    // 실패하면, 코루틴에게 예외를 던지며 작업을 재개
                    continuation.resumeWithException(task.exception ?: Exception("FID aail."))
                }
            }
        }
    }
}