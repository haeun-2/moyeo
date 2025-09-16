package com.mo.moyeo.domain.box.settlement.service;

import com.mo.moyeo.common.exception.CustomException;
import com.mo.moyeo.common.exception.ErrorCode;
import com.mo.moyeo.domain.box.box.service.BoxService;
import com.mo.moyeo.domain.box.settlement.dto.BoxSettleRequest;
import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.service.BoxMemberService;
import com.mo.moyeo.domain.transaction.transfer.dto.TransferRequest;
import com.mo.moyeo.domain.transaction.transfer.service.TransferService;
import com.mo.moyeo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoxSettlementService {

    private final BoxService boxService;
    private final BoxMemberService boxMemberService;
    private final TransferService transferService;

    @Transactional
    public void settleBox(Long boxId, User user, List<BoxSettleRequest> requests) {
        Box box = boxService.getBoxById(boxId);
        if (!box.isOwner(user.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "모임주만 정산 요청을 할 수 있습니다.");
        }

        for (var request: requests) {
            // 정산할 멤버의 개인 박스 조회
            Box memberBox = boxMemberService.getPersonalBoxByBoxMemberId(request.getBoxMemberId());
            // 정산 금액 만큼 모임 박스 -> 멤버 박스로 이체
            TransferRequest transferRequest = TransferRequest.builder()
                    .fromBoxId(boxId)
                    .toBoxId(memberBox.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrencyType())
                    .build();
            transferService.transfer(user, transferRequest);
        }

    }

}
