package com.mo.moyeo.domain.fcm.repository;

import com.mo.moyeo.domain.fcm.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    // ===== 토큰 값으로 찾기 =====
    Optional<FcmToken> findByDeviceToken(String deviceToken);

    // ===== 유저로 찾기 =====
    Optional<FcmToken> findByUserId(Long userId);

    // ===== 모여 박스로 찾기 =====
    @Query("""
        select f.deviceToken
        from FcmToken f
        join f.user u
        join BoxMember bm on bm.user = u
        where bm.box.id = :boxId
          and bm.status = 'JOINED'
    """)
    List<String> findDeviceTokensByBoxId(Long boxId);
}
