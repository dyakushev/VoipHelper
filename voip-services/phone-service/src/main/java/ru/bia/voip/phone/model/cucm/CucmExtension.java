package ru.bia.voip.phone.model.cucm;

import lombok.Builder;
import lombok.Data;
import ru.bia.voip.phone.model.AbstractExtension;

@Data
public class CucmExtension extends AbstractExtension {
    @Builder
    public CucmExtension(String exten) {
        super(exten);
    }
}
