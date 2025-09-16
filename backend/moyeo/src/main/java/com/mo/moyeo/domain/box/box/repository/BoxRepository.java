package com.mo.moyeo.domain.box.box.repository;

import com.mo.moyeo.domain.box.box.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoxRepository extends JpaRepository<Box, Long> {

    @Query("SELECT b.ownerId FROM Box b WHERE b.id = :id")
    Optional<Long> findOwnerIdByBoxId(Long id);

    @Query("""
        SELECT b
        FROM Box b
        LEFT JOIN FETCH b.balances
        WHERE b.ownerId = :ownerId
        AND b.type = 'PERSONAL'
    """)
    Optional<Box> selectPersonalBoxByOwnerId(@Param("ownerId") Long ownerId);

}
