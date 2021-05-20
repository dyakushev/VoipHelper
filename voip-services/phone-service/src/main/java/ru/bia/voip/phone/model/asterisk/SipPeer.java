package ru.bia.voip.phone.model.asterisk;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode
@Data
@Entity
@Table(name = "sippeers")
public class SipPeer extends RepresentationModel<SipPeer> {
    @Id
    private int id;
    @Column(name = "callgroup")
    private String callGroup;
    @Column(name = "ipaddr")
    private String ipAddress;
    @Column(name = "pickupgroup")
    private String pickupGroup;
    @Column(name = "name")
    private String name;


}
