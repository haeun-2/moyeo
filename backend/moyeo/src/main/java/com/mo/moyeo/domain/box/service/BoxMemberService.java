package com.mo.moyeo.domain.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.auth.security.util.AuthenticationUtil;
import com.mo.moyeo.domain.box.dto.BoxMemberResponse;
import com.mo.moyeo.domain.box.dto.BoxMemberUpdatePermissionRequest;
import com.mo.moyeo.domain.box.entity.BoxMember;
import com.mo.moyeo.domain.box.repository.BoxMemberRepository;
import com.mo.moyeo.domain.box.repository.BoxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxMemberService {

    private final BoxRepository boxRepository;
    private final BoxMemberRepository boxMemberRepository;

    public List<BoxMemberResponse> getGroupBoxMemberList(Long boxId) {
        Long ownerId = boxRepository.findOwnerIdByBoxId(boxId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        List<BoxMember> boxMembers = boxMemberRepository.findJoinedMembersByBoxId(boxId);
        return BoxMemberResponse.from(boxMembers, ownerId);
    }

    @Transactional
    public void updateGroupBoxMemberPermissions(Long boxId, List<BoxMemberUpdatePermissionRequest> requests) {
        // 0. 수정할 수 있는지 확인
        // 모임주만 권한을 수정할 수 있음 - 유저가 모임주가 아니라면 수정 불가
        Long ownerId = boxRepository.findOwnerIdByBoxId(boxId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        Long loginUserId = AuthenticationUtil.getCurrentUserId();
        if (!ownerId.equals(loginUserId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "모임주만 멤버 권한을 수정할 수 있습니다.");
        }

        // 1. ID 목록 추출
        List<Long> boxMemberIds = requests.stream().map(BoxMemberUpdatePermissionRequest::getBoxMemberId).collect(Collectors.toList());
        // 2. DB 조회 -> Map 변환
        Map<Long, BoxMember> boxMemberMap = boxMemberRepository.findAllById(boxMemberIds)
                .stream()
                .collect(Collectors.toMap(BoxMember::getId, Function.identity()));

        // 3. 멤버별 권한 수정
        for (var request : requests) {
            BoxMember boxMember = boxMemberMap.get(request.getBoxMemberId());
            if (boxMember == null) {
                throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "모임 멤버를 찾을 수 없습니다.");
            }
            boxMember.updatePermissions(request.getCanTransfer(), request.getCanPayment(), request.getCanExchange());
        }
    }

    @Transactional
    public void leaveGroupBox(Long boxId) {
        Long ownerId = boxRepository.findOwnerIdByBoxId(boxId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        Long loginUserId = AuthenticationUtil.getCurrentUserId();
        BoxMember boxMember = boxMemberRepository.findByBox_IdAndUser_Id(boxId, loginUserId).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        // 모임주는 탈퇴할 수 없음
        if (ownerId.equals(loginUserId)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "모임주는 모임을 탈퇴할 수 없습니다.");
        }
        boxMember.leave();
    }

}
