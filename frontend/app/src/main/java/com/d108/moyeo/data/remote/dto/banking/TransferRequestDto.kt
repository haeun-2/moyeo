package com.d108.moyeo.data.remote.dto.banking

import com.google.gson.annotations.SerializedName

data class TransferRequestDto(

    @SerializedName("fromBoxId")
    val fromBoxId: Long,

    @SerializedName("toBoxId")
    val toBoxId: Long,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("amount")
    val amount: Long
)