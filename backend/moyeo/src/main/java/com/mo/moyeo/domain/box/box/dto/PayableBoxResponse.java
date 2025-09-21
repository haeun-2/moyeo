package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class PayableBoxResponse {

    private Long boxId;
    private String name;
    private List<BalanceDto> balances;
    private String type;
    private Boolean isBookmarked;

    public static PayableBoxResponse from(Box box) {
        return PayableBoxResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(BalanceDto.from(box.getBalances()))
                .type(box.getType().name())
                .build();
    }


    public static PayableBoxResponse from(BoxMember boxMember) {
        return PayableBoxResponse.builder()
                .boxId(boxMember.getBox().getId())
                .name(boxMember.getBox().getBoxName())
                .balances(BalanceDto.from(boxMember.getBox().getBalances()))
                .type(boxMember.getBox().getType().name())
                .isBookmarked(boxMember.getIsBookmarked())
                .build();
    }

    public static List<PayableBoxResponse> from(List<BoxMember> boxMembers) {
        return boxMembers.stream().map(PayableBoxResponse::from).toList();
    }

}
