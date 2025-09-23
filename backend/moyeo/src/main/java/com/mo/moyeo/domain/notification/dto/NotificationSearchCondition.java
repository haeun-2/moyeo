package com.mo.moyeo.domain.notification.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationSearchCondition {

    @Min(value = 0, message = "page는 0 이상이어야 합니다.")
    private Integer page;
    @Min(value = 1, message = "size는 1개 이상이어야 합니다.")
    private Integer size;
    private NotificationSearchCondition.SortDirection sortDir;

    public enum SortDirection {
        DESC, ASC
    }
}
