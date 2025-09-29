package com.d108.moyeo.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

data class MeDto(
    @SerializedName("name")
    val name: String,
)
