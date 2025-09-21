package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.history.PaginatedHistoryResponseDto
import com.d108.moyeo.data.remote.dto.history.UpdateHistoryRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface BoxHistoryService {

    @GET("api/boxes/{boxId}/transactions/histories")
    suspend fun getTransactionHistories(
        @Path("boxId") boxId: Long,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("keyword") keyword: String,
        @Query("type") type: String,
        @Query("categoryId") categoryId: Long?,
        @Query("currency") currency: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sortDir") sortDir: String
    ): Response<PaginatedHistoryResponseDto>


    @PATCH("api/boxes/{boxId}/transactions/histories/{historyId}")
    suspend fun updateHistory(
        @Path("boxId") boxId: Long,
        @Path("historyId") historyId: Long,
        @Body body: UpdateHistoryRequestDto
    ): Response<Unit> // 성공 시 별도 내용이 없으므로 Unit
}