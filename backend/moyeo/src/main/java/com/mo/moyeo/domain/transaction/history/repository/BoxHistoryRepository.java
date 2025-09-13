package com.mo.moyeo.domain.transaction.history.repository;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.transaction.history.entity.BoxHistory;
import com.mo.moyeo.domain.transaction.statistics.dto.CategoryStatisticsDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoxHistoryRepository extends JpaRepository<BoxHistory, Long>, BoxHistoryRepositoryCustom {

    @Query("""
        SELECT new com.mo.moyeo.domain.transaction.statistics.dto.CategoryStatisticsDto(
            c.id,
            c.name,
            abs(sum(bh.amount))
        )
        FROM BoxHistory bh
        JOIN bh.category c
        WHERE bh.box.id = :boxId
          AND bh.amount < 0
          AND bh.currencyCode = :currencyCode
          AND bh.createdAt BETWEEN :startDate AND :endDate
        GROUP BY c.id, c.name
    """)
    List<CategoryStatisticsDto> findCategoryStatistics(
            @Param("boxId") Long boxId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("currencyCode") CurrencyType currencyCode
    );

    @EntityGraph(attributePaths = "transaction")
    Optional<BoxHistory> findWithTransactionById(Long id);

}
