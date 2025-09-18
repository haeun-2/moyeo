package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.member.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GroupBoxResponse {

    private Long boxId;
    private String name;
    private List<BalanceDto> balances;
    private String type;
    private Boolean isBookmarked;

    public static GroupBoxResponse from(BoxMember boxMember) {
        return GroupBoxResponse.builder()
                .boxId(boxMember.getBox().getId())
                .name(boxMember.getBox().getBoxName())
                .balances(BalanceDto.from(boxMember.getBox().getBalances()))
                .type(boxMember.getBox().getType().name())
                .isBookmarked(boxMember.getIsBookmarked())
                .build();
    }

    public static List<GroupBoxResponse> from(List<BoxMember> boxMembers) {
        return boxMembers.stream().map(GroupBoxResponse::from).toList();
    }

}
