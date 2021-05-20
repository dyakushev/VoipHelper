package ru.bia.voip.phone.model.cucm.rest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JabberResponse {
    private String extension;
}
