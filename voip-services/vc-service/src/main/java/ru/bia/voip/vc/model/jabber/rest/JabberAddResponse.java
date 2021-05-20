package ru.bia.voip.vc.model.jabber.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import ru.bia.voip.vc.model.jabber.JabberDevice;
import ru.bia.voip.vc.model.jabber.JabberLine;
import ru.bia.voip.vc.model.jabber.JabberUser;

import java.util.List;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JabberAddResponse {
    private JabberUser jabberUser;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<JabberDevice> jabberDevices;
    private JabberLine jabberLine;
}
