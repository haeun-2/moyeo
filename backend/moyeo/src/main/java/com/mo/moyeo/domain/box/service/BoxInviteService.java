package com.mo.moyeo.domain.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.dto.BoxInviteDto;
import com.mo.moyeo.domain.box.dto.BoxInviteInfoResponse;
import com.mo.moyeo.domain.box.dto.BoxInviteResponse;
import com.mo.moyeo.domain.box.dto.BoxJoinResponse;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxMember;
import com.mo.moyeo.domain.box.repository.BoxMemberRepository;
import com.mo.moyeo.domain.box.repository.BoxRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxInviteService {

    private final BoxRepository boxRepository;
    private final BoxMemberRepository boxMemberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BOX_INVITE_KEY = "box_invite_code::";

    @Value("${moyeo.server.base_url}")
    private String baseUrl;

    public BoxInviteResponse createBoxInviteLink(Long boxId, User user) {
        Box box = boxRepository.findById(boxId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        boxMemberRepository.findByBoxIdAndUserId(boxId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.ACCESS_DENIED)); // 모임 멤버인지 확인

        // 1. UUID로 초대코드 생성
        String inviteCode = UUID.randomUUID().toString();

        // 2. redis에 저장 (key - box_invite_code::${code}, value - { boxId, boxName, expiresAt }, ttl - 1일)
        String key = BOX_INVITE_KEY + inviteCode;
        BoxInviteDto boxInviteDto = new BoxInviteDto(boxId, box.getBoxName(), LocalDateTime.now().plusDays(1));
        redisTemplate.opsForValue().set(key, boxInviteDto, Duration.ofDays(1));

        // 3. 응답 리턴
        return BoxInviteResponse.from(baseUrl, inviteCode, boxInviteDto.getExpiresAt());
    }

    @Transactional
    public BoxJoinResponse joinBoxByInviteLink(String code, User user) {
        // 1. redis에서 초대 정보 조회
        BoxInviteDto boxInviteDto = (BoxInviteDto) redisTemplate.opsForValue().get(BOX_INVITE_KEY + code);
        if (boxInviteDto == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않거나 만료된 초대 링크입니다");
        }

        // 2. 모임에 가입
        Optional<BoxMember> existingMember = boxMemberRepository.findByBoxIdAndUserId(boxInviteDto.getBoxId(), user.getId());
        // 이전 가입 이력이 있는 경우
        if (existingMember.isPresent()) {
            BoxMember boxMember = existingMember.get();
            if (!boxMember.isMember()) boxMember.join(); // 재가입
        } 
        // 새로운 가입
        else {
            Box box = boxRepository.findById(boxInviteDto.getBoxId()).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));

            BoxMember boxMember = new BoxMember(box, user);
            boxMemberRepository.save(boxMember);
        }

        // 3. 응답 리턴
        return new BoxJoinResponse(boxInviteDto.getBoxId());
    }

    public BoxInviteInfoResponse getInviteInfo(String code) {
        // redis에서 초대 정보 조회하여 반환
        BoxInviteDto boxInviteDto = (BoxInviteDto) redisTemplate.opsForValue().get(BOX_INVITE_KEY + code);
        return BoxInviteInfoResponse.from(boxInviteDto);
    }

}
