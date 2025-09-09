package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.Box;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class BoxResponse {

    private Long boxId;
    private String name;
    private List<Balance> balances;
    private String type;

    public static BoxResponse from(Box box) {
        return BoxResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(Balance.from(box.getBalances()))
                .type(box.getType().name())
                .build();
    }

}
