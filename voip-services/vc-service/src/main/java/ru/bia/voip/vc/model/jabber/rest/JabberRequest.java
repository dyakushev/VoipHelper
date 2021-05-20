package ru.bia.voip.vc.model.jabber.rest;

import lombok.Data;
import ru.bia.voip.vc.controller.validation.ValueOfEnum;
import ru.bia.voip.vc.model.jabber.JabberType;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class JabberRequest {
    @NotNull
    @Size(min = 1, max = 50)
    private String userName;
    @Size(min = 1, max = 4)
    @NotNull
    private Set<@ValueOfEnum(enumClass = JabberType.class, message = "") String> types;
}
