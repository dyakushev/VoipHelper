package ru.bia.voip.vc.model.jabber;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JabberLine {
    private String uid;
    private String dirNumber;
}
