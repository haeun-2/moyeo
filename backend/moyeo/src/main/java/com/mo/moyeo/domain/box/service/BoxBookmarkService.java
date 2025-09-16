package com.mo.moyeo.domain.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.dto.GroupBoxResponse;
import com.mo.moyeo.domain.box.dto.MyBoxResponse;
import com.mo.moyeo.domain.box.entity.BoxMember;
import com.mo.moyeo.domain.box.repository.BoxMemberRepository;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxBookmarkService {

    private final BoxMemberRepository boxMemberRepository;

    @Transactional
    public void bookmarkBox(Long boxId, User user) {
        BoxMember boxMember = boxMemberRepository.findJoinedByBoxIdAndUserId(boxId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        boxMember.bookmark();
    }

    @Transactional
    public void removeBookmarkBox(Long boxId, User user) {
        BoxMember boxMember = boxMemberRepository.findJoinedByBoxIdAndUserId(boxId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        boxMember.unbookmark();
    }

    public List<GroupBoxResponse> getAllBookmarkBoxList(User user) {
        List<BoxMember> boxMembers = boxMemberRepository.findBookMarkedBoxAll(user);
        return GroupBoxResponse.from(boxMembers);
    }

}
