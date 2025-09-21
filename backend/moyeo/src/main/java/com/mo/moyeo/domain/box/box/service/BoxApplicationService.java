package com.mo.moyeo.domain.box.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.box.dto.*;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import com.mo.moyeo.domain.box.balance.service.BoxBalanceService;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxApplicationService {

    private final BoxService boxService;
    private final BoxMemberService boxMemberService;
    private final BoxBalanceService boxBalanceService;

    private static final int MAX_BOX_COUNT = 30;

    public MyBoxResponse getMyBox(User user) {
        Box box = boxService.getPersonalBoxByUserId(user.getId());
        return MyBoxResponse.from(box);
    }

    public List<GroupBoxResponse> getGroupBoxList(User user) {
        List<BoxMember> boxes = boxMemberService.getJoinedGroupBoxByUser(user);
        return GroupBoxResponse.from(boxes);
    }

    public BoxDetailResponse getBoxDetail(Long boxId, User user) {
        Box box = boxService.getBoxWithBalanceById(boxId);
        BoxPermissionResponse myBoxPermissions = getMyBoxPermissions(box.getId(), user);
        List<BoxMember> boxMembers = boxMemberService.getJoinedMembersByBoxId(boxId);
        return BoxDetailResponse.from(box, myBoxPermissions, boxMembers);
    }

    public BoxPermissionResponse getMyBoxPermissions(Long boxId, User user) {
        Box box = boxService.getBoxById(boxId);

        if (box.isPersonal() && box.isOwner(user.getId())) {
            return BoxPermissionResponse.from(box);
        }

        BoxMember boxMember = boxMemberService.getJoinedBoxMember(boxId, user.getId());
        return BoxPermissionResponse.from(boxMember, box.isOwner(user.getId()));
    }

    public void createPersonalBox(User user) {
        // 개인 박스 생성
        Box box = new Box(String.format("%s의 박스", user.getName()), user.getId(), Box.Type.PERSONAL);
        boxService.save(box);
    }

    @Transactional
    public BoxCreateResponse createGroupBox(User user, BoxCreateRequest request) {
        // 1. 박스 최대 개수 제한 체크
        Integer count = boxMemberService.getCountJoinedGroupByUser(user);
        if (count >= MAX_BOX_COUNT) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "모임 박스는 30개까지 가입할 수 있습니다.");
        }

        // 2. 모입 박스 생성
        Box box = new Box(request.getName(), user.getId(), Box.Type.GROUP);
        boxService.save(box);

        // 3. 모임주 멤버로 저장
        boxMemberService.saveBoxOwner(box, user);

        return BoxCreateResponse.from(box);
    }

    public List<PayableBoxResponse> getPayableBoxList(User user) {
        Box personalBox = boxService.getPersonalBoxByUserId(user.getId());
        List<BoxMember> boxMembers = boxMemberService.getJoinedPayableGroupBoxByUser(user);

        List<PayableBoxResponse> response = new ArrayList<>(PayableBoxResponse.from(boxMembers));
        response.add(0, PayableBoxResponse.from(personalBox));

        return response;
    }

}
