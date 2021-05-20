package ru.bia.voip.phone.model.asterisk;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.bia.voip.phone.model.AbstractExtension;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "extensions")
@NoArgsConstructor
public class AsteriskExtension extends AbstractExtension {
    @Id
    private int id;
    private String context;
    private short priority;
    private String app;
    private String appdata;

    @Builder
    public AsteriskExtension(int id, String context, short priority, String app, String appdata, String exten) {
        super(exten);
        this.id = id;
        this.context = context;
        this.priority = priority;
        this.app = app;
        this.appdata = appdata;
    }
}
