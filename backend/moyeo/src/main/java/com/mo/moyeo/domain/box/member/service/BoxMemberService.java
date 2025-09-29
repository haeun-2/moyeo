package com.mo.moyeo.domain.box.member.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.box.member.dto.BoxPermissionDto;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import com.mo.moyeo.domain.box.member.repository.BoxMemberRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxMemberService {

    private final BoxService boxRepository;
    private final BoxMemberRepository boxMemberRepository;

    public BoxPermissionDto getBoxPermission(Box box, User user){
        if (box.isPersonalOwner(user.getId())) {
            return BoxPermissionDto.fromAllTrue();
        }
        BoxMember boxMember = boxMemberRepository.findJoinedByBoxIdAndUserId(box.getId(), user.getId()).orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));
        return BoxPermissionDto.from(boxMember);
    }

    public void validateTransferPermission(Box box, User user) {
        if (!getBoxPermission(box, user).getCanTransfer()) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void validatePaymentPermission(Box box, User user) {
        if (!getBoxPermission(box, user).getCanPayment()) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void validateExchangePermission(Box box, User user) {
        if (!getBoxPermission(box, user).getCanExchange()) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    public Optional<BoxMember> findByBoxIdAndUserId(Long boxId, Long userId) {
        return boxMemberRepository.findByBoxIdAndUserId(boxId, userId);
    }

    public Integer getCountJoinedGroupByUser(User user) {
        return boxMemberRepository.countJoinedGroupByUser(user);
    }

    public Box getPersonalBoxByBoxMemberId(Long boxMemberId) {
        return boxMemberRepository.findPersonalBoxByBoxMemberId(boxMemberId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    public List<BoxMember> getJoinedGroupBoxByUser(User user) {
        return boxMemberRepository.findJoinedGroupBoxByUser(user);
    }

    public List<BoxMember> getJoinedPayableGroupBoxByUser(User user) {
        return boxMemberRepository.findJoinedPayableGroupBoxByUser(user);
    }

    public List<BoxMember> getJoinedExchangeableGroupBoxByUser(User user) {
        return boxMemberRepository.findJoinedExchangeableGroupBoxByUser(user);
    }

    public BoxMember getJoinedBoxMember(Long boxId, Long userId) {
        return boxMemberRepository.findJoinedByBoxIdAndUserId(boxId, userId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }

    public void validateJoinedBoxMember(Box box, User user) {
        if (box.isPersonalOwner(user.getId())) return;
        if (!boxMemberRepository.existsJoinedBoxMember(box, user)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    public List<BoxMember> getBookmarkedBoxMembers(User user) {
        return boxMemberRepository.findBookMarkedBoxAll(user);
    }

    public List<BoxMember> getJoinedMembersByBoxId(Long boxId) {
        return boxMemberRepository.findJoinedMembersByBoxId(boxId);
    }

    public List<BoxMember> getMembersById(List<Long> boxMemberIds) {
        return boxMemberRepository.findAllById(boxMemberIds);
    }

    @Transactional
    public void saveBoxOwner(Box box, User user) {
        boxMemberRepository.save(new BoxMember(box, user, true));
    }

    @Transactional
    public void saveBoxMember(Box box, User user) {
        boxMemberRepository.save(new BoxMember(box, user));
    }
}
