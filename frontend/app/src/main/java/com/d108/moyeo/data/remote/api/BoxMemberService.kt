package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.box.BoxMemberDto
import com.d108.moyeo.data.remote.dto.box.UpdatePermissionRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface BoxMemberService {
    @GET("api/boxes/{boxId}/members")
    suspend fun getBoxMembers(@Path("boxId") boxId: Long): Response<List<BoxMemberDto>>  // 리모트 DTO


    @PATCH("api/boxes/{boxId}/members/permissions")
    suspend fun updateBoxMemberPermission(
        @Path("boxId") boxId: Long,
        @Body requestList: List<UpdatePermissionRequestDto>
    ): Response<Unit>
}