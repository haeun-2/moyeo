package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.Box;
import com.mo.moyeo.domain.box.entity.BoxMember;
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
    private List<Balance> balances;
    private String type;
    private Boolean isBookmarked;

    public static GroupBoxResponse from(BoxMember boxMember) {
        return GroupBoxResponse.builder()
                .boxId(boxMember.getBox().getId())
                .name(boxMember.getBox().getBoxName())
                .balances(Balance.from(boxMember.getBox().getBalances()))
                .type(boxMember.getBox().getType().name())
                .isBookmarked(boxMember.getIsBookmarked())
                .build();
    }

    public static List<GroupBoxResponse> from(List<BoxMember> boxMembers) {
        return boxMembers.stream().map(GroupBoxResponse::from).toList();
    }

}
