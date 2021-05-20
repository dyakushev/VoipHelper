package ru.bia.voip.phone.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public abstract class AbstractExtension extends RepresentationModel<AbstractExtension> {
    private String exten;

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten;
    }


}
