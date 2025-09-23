package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.banking.SettlementRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface BoxSettlementService {
    @POST("/api/boxes/{boxId}/settlements")
    suspend fun settleBox(@Path("boxId") boxId: Long, @Body settlements: List<SettlementRequestDto>): Response<Unit>
}