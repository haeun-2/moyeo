package com.mo.moyeo.domain.box.box.dto;

import com.mo.moyeo.domain.box.box.entity.Box;
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

