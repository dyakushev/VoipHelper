package ru.bia.voip.phone.model.cucm;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@Builder
public class JabberDevice {
    private String deviceName;
    private String extension;
    private String userName;
}