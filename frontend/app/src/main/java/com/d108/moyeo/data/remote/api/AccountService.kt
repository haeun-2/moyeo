package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.account.AccountConnectRequestDto
import com.d108.moyeo.data.remote.dto.account.AccountVerificationRequestDto
import com.d108.moyeo.data.remote.dto.account.ConnectedAccountResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountService {

    @GET("api/v1/users/accounts")
    suspend fun getConnectedAccount(): ConnectedAccountResponseDto

    @POST("api/v1/users/accounts/verification")
    suspend fun requestAccountVerification(
        @Body body: AccountVerificationRequestDto
    ): Response<Unit>

    @POST("api/v1/users/accounts")
    suspend fun connectAccount(
        @Body body: AccountConnectRequestDto
    ): Response<Unit>
}