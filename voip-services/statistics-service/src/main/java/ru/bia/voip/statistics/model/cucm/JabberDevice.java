package ru.bia.voip.statistics.model.cucm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JabberDevice {
    private String deviceName;
    private String extension;
    private String userName;
}
