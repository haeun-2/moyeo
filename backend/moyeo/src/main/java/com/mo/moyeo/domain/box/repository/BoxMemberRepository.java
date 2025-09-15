package com.mo.moyeo.domain.box.repository;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxMember;
import com.mo.moyeo.domain.user.entity.User;
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

    @Query("""
        SELECT b
        FROM BoxMember bm
        JOIN bm.user u
        JOIN Box b ON b.ownerId = u.id AND b.type = 'PERSONAL'
        WHERE bm.id = :boxMemberId
          AND bm.box.id = :boxId
    """)
    Optional<Box> findPersonalBoxByBoxMemberIdAndBoxId(@Param("boxMemberId") Long boxMemberId, @Param("boxId") Long boxId);

    @Query("SELECT COUNT(bm) FROM BoxMember bm WHERE bm.user = :user AND bm.status = 'JOINED'")
    Integer countJoinedGroupByUser(User user);
}
