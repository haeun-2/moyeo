package com.mo.moyeo.domain.box.dto;

import com.mo.moyeo.domain.box.entity.Box;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoxCreateResponse {

        private Long boxId;

        public static BoxCreateResponse from(Box box) {
                return new BoxCreateResponse(box.getId());
        }
}

