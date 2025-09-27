package com.d108.moyeo.data.remote.dto.exchange

import com.google.gson.annotations.SerializedName

data class ExchangeRequestDto(

    @SerializedName("fromBoxId")
    val fromBoxId: Long,

    @SerializedName("fromCurrency")
    val fromCurrency: String,

    @SerializedName("toCurrency")
    val toCurrency: String,

    @SerializedName("amount")
    val amount: Long
)