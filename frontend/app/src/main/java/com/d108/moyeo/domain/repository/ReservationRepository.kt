package com.d108.moyeo.domain.repository

import com.d108.moyeo.domain.model.exchange.Reservation

interface ReservationRepository {
    suspend fun getReservationsByBox(boxId: Long): Result<List<Reservation>>
}