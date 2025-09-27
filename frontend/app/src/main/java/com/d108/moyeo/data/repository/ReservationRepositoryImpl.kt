package com.d108.moyeo.data.repository

import com.d108.moyeo.data.mapper.toDomain
import com.d108.moyeo.data.remote.api.ExchangeService
import com.d108.moyeo.domain.model.exchange.Reservation
import com.d108.moyeo.domain.repository.ReservationRepository
import javax.inject.Inject

class ReservationRepositoryImpl @Inject constructor(
    private val api: ExchangeService
) : ReservationRepository {

    override suspend fun getReservationsByBox(boxId: Long): Result<List<Reservation>> {
        return runCatching {
            val response = api.getReservationsByBox(boxId)
            if (!response.isSuccessful) {
                throw IllegalStateException("HTTP ${response.code()}: ${response.message()}")
            }
            val body = response.body() ?: emptyList()
            body.map { it.toDomain() }
        }
    }
}