package com.mo.moyeo.domain.box.bookmark.service;

import com.mo.moyeo.domain.box.box.dto.GroupBoxResponse;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxBookmarkService {

    private final BoxMemberService boxMemberService;

    @Transactional
    public void bookmarkBox(Long boxId, User user) {
        BoxMember boxMember = boxMemberService.getJoinedBoxMember(boxId, user.getId());
        boxMember.bookmark();
    }

    @Transactional
    public void removeBookmarkBox(Long boxId, User user) {
        BoxMember boxMember = boxMemberService.getJoinedBoxMember(boxId, user.getId());
        boxMember.unbookmark();
    }

    public List<GroupBoxResponse> getAllBookmarkBoxList(User user) {
        List<BoxMember> boxMembers = boxMemberService.getBookmarkedBoxMembers(user);
        return GroupBoxResponse.from(boxMembers);
    }

}
