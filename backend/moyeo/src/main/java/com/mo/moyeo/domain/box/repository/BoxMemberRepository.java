package com.mo.moyeo.domain.box.repository;

import com.mo.moyeo.domain.box.entity.BoxMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoxMemberRepository extends JpaRepository<BoxMember, Long> {

    @Query("""
            SELECT bm
            FROM BoxMember bm
            WHERE bm.box.id = :boxId 
            AND bm.user.id = :userId
    """)
    Optional<BoxMember> findByBoxIdAndUserId(@Param("boxId") Long boxId, @Param("userId") Long userId);

    @Query("""
        SELECT bm
        FROM BoxMember bm
        JOIN FETCH bm.user
        WHERE bm.box.id = :boxId
        AND bm.status = 'JOINED'
    """)
    List<BoxMember> findJoinedMembersByBoxId(@Param("boxId") Long boxId);

}
