package com.mo.moyeo.domain.box.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.common.paging.PageResponse;
import com.mo.moyeo.domain.box.dto.BoxCreateRequest;
import com.mo.moyeo.domain.box.dto.BoxCreateResponse;
import com.mo.moyeo.domain.box.dto.BoxPermissionResponse;
import com.mo.moyeo.domain.box.dto.BoxResponse;
import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxBalance;
import com.mo.moyeo.domain.box.entity.BoxMember;
import com.mo.moyeo.domain.box.repository.BoxBalanceRepository;
import com.mo.moyeo.domain.box.repository.BoxMemberRepository;
import com.mo.moyeo.domain.box.repository.BoxRepository;
import com.mo.moyeo.domain.currency.entity.CurrencyType;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoxService {

    private final BoxRepository boxRepository;
    private final BoxMemberRepository boxMemberRepository;
    private final BoxBalanceRepository boxBalanceRepository;

    public BoxResponse getMyBox(User user) {
        Box box = boxRepository.selectPersonalBoxByOwnerId(user.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        return BoxResponse.from(box);
    }

    public PageResponse<BoxResponse> getGroupBoxList(User user, int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        Slice<Box> boxes = boxRepository.selectJoinedGroupBoxByUserId(user.getId(), pageable);
        for (Box box: boxes) {
            box.getBalances();
        }
        return PageResponse.from(boxes, BoxResponse::from);
    }

    public BoxPermissionResponse getMyBoxPermissions(Long boxId, User user) {
        Box box = boxRepository.findById(boxId).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        if (box.isPersonal() && box.getOwnerId().equals(user.getId())) {
            return BoxPermissionResponse.from(box);
        }

        BoxMember boxMember = boxMemberRepository.findByBoxIdAndUserId(boxId, user.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOX_NOT_FOUND));
        return BoxPermissionResponse.from(boxMember, box.getOwnerId().equals(user.getId()));
    }

    @Transactional
    public void createPersonalBox(User user) {
        // 1. 개인 박스 생성
        Box box = new Box(String.format("%s의 박스", user.getName()), user.getId(), Box.Type.PERSONAL);
        boxRepository.save(box);

        // 2. 박스 잔액 초기화 (한화 + 외화)
        List<BoxBalance> boxBalances = Arrays.stream(CurrencyType.values())
                .map(type -> new BoxBalance(box, type))
                .toList();
        boxBalanceRepository.saveAll(boxBalances);
    }

    @Transactional
    public BoxCreateResponse createGroupBox(User user, BoxCreateRequest request) {
        // 1. 모입 박스 생성
        Box box = new Box(request.getName(), user.getId(), Box.Type.GROUP);
        boxRepository.save(box);

        // 2. 모임주 멤버로 저장
        boxMemberRepository.save(new BoxMember(box, user, true));

        // 3. 박스 잔액 초기화 (한화 + 외화)
        List<BoxBalance> boxBalances = Arrays.stream(CurrencyType.values())
                .map(type -> new BoxBalance(box, type))
                .toList();
        boxBalanceRepository.saveAll(boxBalances);

        return BoxCreateResponse.from(box);
    }

    public Box getBoxById(Long boxId){
        return boxRepository.findById(boxId)
                .orElseThrow(()-> new CustomException(ErrorCode.BOX_NOT_FOUND));
    }
}
