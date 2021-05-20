package ru.bia.voip.phone.model.cucm.rest;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JabberRequest implements Serializable {
    @NotNull
    @Size(min = 1, max = 50)
    private String username;

}
