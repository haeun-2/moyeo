package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.box.entity.Box;
import com.mo.moyeo.domain.box.member.dto.BoxMemberResponse;
import com.mo.moyeo.domain.box.member.entity.BoxMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BoxDetailResponse {

    private Long boxId;
    private String name;
    private List<BalanceDto> balances;
    private String type;
    private BoxPermissionResponse permission;
    private List<BoxMemberResponse> members;

    public static BoxDetailResponse from(Box box, BoxPermissionResponse permissionResponse, List<BoxMember> boxMembers) {
        return BoxDetailResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(BalanceDto.from(box.getBalances()))
                .type(box.getType().name())
                .permission(permissionResponse)
                .members(BoxMemberResponse.from(boxMembers, box.getOwnerId()))
                .build();
    }

}
