package com.mo.moyeo.domain.box.member.repository;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
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
            WHERE bm.box.id = :boxId
            AND bm.user.id = :userId
            AND bm.status = 'JOINED'
    """)
    Optional<BoxMember> findJoinedByBoxIdAndUserId(@Param("boxId") Long boxId, @Param("userId") Long userId);

    @Query("""
        SELECT DISTINCT bm
        FROM BoxMember bm
        JOIN FETCH bm.box b
        LEFT JOIN FETCH b.balances bal
        WHERE bm.user = :user
          AND bm.status = 'JOINED'
          AND bm.isBookmarked = true
          AND b.type = 'GROUP'
        ORDER BY b.createdAt DESC, b.id DESC
    """)
    List<BoxMember> findBookMarkedBoxAll(@Param("user") User user);


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
    """)
    Optional<Box> findPersonalBoxByBoxMemberId(@Param("boxMemberId") Long boxMemberId);

    @Query("SELECT COUNT(bm) FROM BoxMember bm WHERE bm.user = :user AND bm.status = 'JOINED'")
    Integer countJoinedGroupByUser(User user);

    @Query("""
        SELECT DISTINCT bm
        FROM BoxMember bm
        JOIN FETCH bm.box b
        LEFT JOIN FETCH b.balances bal
        WHERE bm.user = :user
          AND bm.status = 'JOINED'
          AND b.type = 'GROUP'
        ORDER BY bm.isBookmarked DESC, b.createdAt DESC, b.id DESC
    """)
    List<BoxMember> findJoinedGroupBoxByUser(@Param("user") User user);

    @Query("""
        SELECT DISTINCT bm
        FROM BoxMember bm
        JOIN FETCH bm.box b
        LEFT JOIN FETCH b.balances bal
        WHERE bm.user = :user
          AND bm.status = 'JOINED'
          AND bm.canPayment = true
          AND b.type = 'GROUP'
        ORDER BY bm.isBookmarked DESC, b.createdAt DESC, b.id DESC
    """)
    List<BoxMember> findJoinedPayableGroupBoxByUser(@Param("user") User user);


    @Query("""
        SELECT CASE WHEN COUNT(bm) > 0 THEN true ELSE false END
        FROM BoxMember bm
        WHERE bm.box = :box
          AND bm.user = :user
          AND bm.status = 'JOINED'
    """)
    boolean existsJoinedBoxMember(Box box, User user);

}
