package ru.bia.voip.statistics.model.asterisk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties
@Data
@NoArgsConstructor
public class AsteriskExtension {
    private String exten;
    private int id;
}
