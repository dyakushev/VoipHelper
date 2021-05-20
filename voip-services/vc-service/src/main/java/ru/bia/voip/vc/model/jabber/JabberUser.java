package ru.bia.voip.vc.model.jabber;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JabberUser {
    private String userName;
    private String password;
    private String uid;
    private UserType userType;
}
