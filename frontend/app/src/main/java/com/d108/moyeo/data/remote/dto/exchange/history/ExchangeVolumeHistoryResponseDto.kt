package com.d108.moyeo.data.remote.dto.exchange.history

import com.google.gson.annotations.SerializedName


/**
 *  {
 *     "recordedAt": "2025-09-24T01:30:10.327Z",
 *     "totalAmount": 0
 *   }
 */
data class ExchangeVolumeHistoryResponseDto(
    @SerializedName("recordedAt")
    val recordedAt: String,
    @SerializedName("totalAmount")
    val totalAmount: Double
)

