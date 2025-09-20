package com.d108.moyeo.data.remote.api

import com.d108.moyeo.data.remote.dto.box.BoxDetailResponseDto
import com.d108.moyeo.data.remote.dto.box.CreateBoxRequestDto
import com.d108.moyeo.data.remote.dto.box.CreateBoxResponseDto
import com.d108.moyeo.data.remote.dto.box.GroupBoxResponseDto
import com.d108.moyeo.data.remote.dto.box.PersonalBoxResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BoxService {

    // 개인 박스 호출
    @GET("api/boxes/me")
    suspend fun getPersonalBox(): Response<PersonalBoxResponseDto>


    // 모임 박스 관련
    // 호출
    @GET("api/boxes")
    suspend fun getGroupBoxes(
        @Query("size") size: Int
    ): Response<List<GroupBoxResponseDto>>

    // 상세 호출
    @GET("api/boxes/{boxId}")
    suspend fun getBoxDetail(@Path("boxId") boxId: Long): Response<BoxDetailResponseDto>

    // 모임 박스 생성
    @POST("api/boxes")
    suspend fun createBox(@Body request: CreateBoxRequestDto) : Response<CreateBoxResponseDto>


    // 모임 박스 즐겨찾기 관련
    // 호출
    @GET("api/boxes/bookmarks")
    suspend fun getBookmarkedBoxes(): Response<List<GroupBoxResponseDto>>

    // 즐겨찾기 등록
    @POST("api/boxes/{boxId}/bookmarks")
    suspend fun addBookmark(@Path("boxId") boxId: Long): Response<Unit>

    // 즐겨찾기 삭제
    @DELETE("api/boxes/{boxId}/bookmarks")
    suspend fun deleteBookmark(@Path("boxId") boxId: Long): Response<Unit>

}
