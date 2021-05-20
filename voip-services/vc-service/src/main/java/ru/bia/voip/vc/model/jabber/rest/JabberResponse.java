package ru.bia.voip.vc.model.jabber.rest;

import lombok.Builder;
import lombok.Data;
import ru.bia.voip.vc.model.jabber.JabberType;

import java.util.Collection;

@Builder
@Data
public class JabberResponse {
    private String deviceName;
    private String userName;
    private JabberType jabberType;
    private Collection<String> dirNumbers;
}
