package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.box.BoxDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BoxService {
    // 개인 박스
    @GET("api/boxes/me")
    suspend fun getMyPersonalBox(): BoxDto

    // 모임 박스 (페이지네이션)
    @GET("api/boxes")
    suspend fun getGroupBoxes(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): List<BoxDto>
}
