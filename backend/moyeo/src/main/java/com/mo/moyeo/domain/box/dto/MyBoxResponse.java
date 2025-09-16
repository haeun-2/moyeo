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
public class MyBoxResponse {

    private Long boxId;
    private String name;
    private List<Balance> balances;
    private String type;

    public static MyBoxResponse from(Box box) {
        return MyBoxResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(Balance.from(box.getBalances()))
                .type(box.getType().name())
                .build();
    }

    public static List<MyBoxResponse> from(List<Box> boxes) {
        return boxes.stream().map(MyBoxResponse::from).toList();
    }

}
