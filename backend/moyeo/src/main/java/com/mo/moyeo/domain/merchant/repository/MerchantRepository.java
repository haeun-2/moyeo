package com.mo.moyeo.domain.merchant.repository;

import com.mo.moyeo.domain.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    @Query("""
        SELECT DISTINCT m
        FROM BoxHistory bh
        JOIN Payment p ON bh.transaction = p.transaction
        JOIN p.merchant m
        WHERE bh.box.id = :boxId
        AND p.status = 'APPROVED'
    """)
    List<Merchant> findPaidMerchantsByBoxId(Long boxId);

}
