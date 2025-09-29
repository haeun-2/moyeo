package com.mo.moyeo.domain.box.box.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoxCreateRequest {

        @NotBlank(message = "모임 이름은 필수입니다.")
        @Size(max = 100, message = "모임 이름 최대 길이는 100자 입니다.")
        private String name;

}
