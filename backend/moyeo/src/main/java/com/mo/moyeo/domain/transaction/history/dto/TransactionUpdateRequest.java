package com.mo.moyeo.domain.transaction.history.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionUpdateRequest {

    @Size(message = "메모는 최대 30자입니다.", max = 30)
    private String memo;
    private Long categoryId;

}
