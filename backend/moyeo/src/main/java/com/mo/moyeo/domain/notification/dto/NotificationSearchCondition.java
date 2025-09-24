package com.mo.moyeo.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
@Builder
public class NotificationSearchCondition {

    @Min(value = 0, message = "page는 0 이상이어야 합니다.")
    private Integer page;
    
    @Min(value = 1, message = "size는 1개 이상이어야 합니다.")
    @Schema(defaultValue = "10")
    private Integer size;

    @Schema(defaultValue = "DESC")
    private Sort.Direction direction;

    public Pageable toPageable() {
        return PageRequest.of(page, size, Sort.by(direction, "receivedAt").and(Sort.by(direction, "id")));
    }
}
