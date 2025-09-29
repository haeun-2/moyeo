package com.mo.moyeo.domain.transaction.history.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Builder
public class TransactionSearchCondition {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String keyword;
    private Type type;
    private Long categoryId;
    private CurrencyType currency;

    @Min(value = 0, message = "page는 0 이상이어야 합니다.")
    private Integer page;
    @Min(value = 1, message = "size는 1개 이상이어야 합니다.")
    private Integer size;
    private SortDirection sortDir;

    public enum Type {
        DEPOSIT, WITHDRAW
    }

    public enum SortDirection {
        ASC, DESC
    }

}
