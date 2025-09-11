package com.mo.moyeo.domain.transaction.history.dto;

import com.mo.moyeo.domain.currency.entity.CurrencyType;
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

    private Integer page;
    private Integer size;
    private SortDirection sortDir;

    public enum Type {
        DEPOSIT, WITHDRAW
    }

    public enum SortDirection {
        ASC, DESC
    }

}
