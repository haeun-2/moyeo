package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Getter
public class BoxMemberResponse {

    private Long boxMemberId;
    private String name;
    private Boolean canTransfer;
    private Boolean canPayment;
    private Boolean canExchange;
    private Boolean isOwner;

    public static BoxMemberResponse from(BoxMember boxMember, Long ownerId) {
        return BoxMemberResponse.builder()
                .boxMemberId(boxMember.getId())
                .name(boxMember.getUser().getName())
                .canTransfer(boxMember.getCanTransfer())
                .canPayment(boxMember.getCanPayment())
                .canExchange(boxMember.getCanExchange())
                .isOwner(boxMember.getUser().getId().equals(ownerId))
                .build();
    }

    public static List<BoxMemberResponse> from(List<BoxMember> boxMembers, Long ownerId) {
        return boxMembers.stream()
                .map(boxMember -> BoxMemberResponse.from(boxMember, ownerId))
                .collect(Collectors.toList());
    }

}
