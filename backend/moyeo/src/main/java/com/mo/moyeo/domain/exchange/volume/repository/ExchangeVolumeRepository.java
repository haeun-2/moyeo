package com.mo.moyeo.domain.exchange.volume.repository;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.exchange.volume.dto.ExchangeVolumeDto;
import com.mo.moyeo.domain.exchange.volume.entity.ExchangeVolume;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExchangeVolumeRepository extends JpaRepository<ExchangeVolume, Long> {

    @Query(value = """
        SELECT ev.recorded_at AS recordedAt,
               ev.amount AS amount
        FROM exchange_volume ev
        WHERE ev.currency_code = :currencyCode
          AND ev.type = :type
        ORDER BY ev.recorded_at asc
""", nativeQuery = true)
    List<ExchangeVolumeProjection> getVolumes(
            @Param("currencyCode") String currencyCode,
            @Param("type") String type
    );
}
