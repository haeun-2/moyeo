package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.box.entity.Box;
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
    private List<BalanceDto> balances;
    private String type;

    public static MyBoxResponse from(Box box) {
        return MyBoxResponse.builder()
                .boxId(box.getId())
                .name(box.getBoxName())
                .balances(BalanceDto.from(box.getBalances()))
                .type(box.getType().name())
                .build();
    }

    public static List<MyBoxResponse> from(List<Box> boxes) {
        return boxes.stream().map(MyBoxResponse::from).toList();
    }

}
