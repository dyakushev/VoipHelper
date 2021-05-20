package ru.bia.voip.vc.model.codec;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodecResponse {
    private String description;
    private String dirNumber;
    private String name;
    private String deviceType;

}
