package ru.bia.voip.phone.model.asterisk;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "queue_member")
public class QueueMember {
    @Id
    private Integer uniqueid;
    @Column(name = "membername")
    private String memberName;
    @Column(name = "queue_name")
    private String queueName;
    @Column(name = "interface")
    private String interfaceField;
    @Column(name = "penalty")
    private Integer penalty;
    @Column(name = "paused")
    private Integer paused;


}
