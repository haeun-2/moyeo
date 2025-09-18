package com.mo.moyeo.domain.fcm.dto;

import com.mo.moyeo.domain.fcm.config.FcmConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class FcmMessageDTO {

    private String title;
    private String body;
    private Map<String, String> data;
    private String type;
}
