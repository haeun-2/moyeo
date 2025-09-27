package com.d108.moyeo.domain.usecase.exchange

import com.d108.moyeo.domain.model.exchange.Reservation
import com.d108.moyeo.domain.repository.ReservationRepository
import javax.inject.Inject

class GetReservationsByBoxUseCase @Inject constructor(
    private val repository: ReservationRepository
) {
    suspend operator fun invoke(boxId: Long): Result<List<Reservation>> {
        return repository.getReservationsByBox(boxId)
    }
}