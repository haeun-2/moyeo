package com.mo.moyeo.domain.exchange.reservation.repository;

import com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto;
import com.mo.moyeo.domain.exchange.reservation.entity.ReservedExchange;
import com.mo.moyeo.domain.transaction.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedExchangeRepository extends JpaRepository<ReservedExchange, Long> {
    @Query("""
    select new com.mo.moyeo.domain.exchange.reservation.dto.ExchangeReserveListDto(
        re.id,
        re.fromCurrency.code,
        re.toCurrency.code,
        re.targetRate,
        re.amount,
        re.expiresAt,
        re.createdAt,
        re.status
    )
    from ReservedExchange re
    where re.box.id = :boxId
    order by re.expiresAt asc
    """)
    List<ExchangeReserveListDto> findReservationList(Long boxId);

    @Query("""
    select re
    from ReservedExchange re
    where re.status = 'WAITING'
    """)
    List<ReservedExchange> findWaitingReservation();

    Optional<ReservedExchange> findByTransaction(Transaction transaction);
}
