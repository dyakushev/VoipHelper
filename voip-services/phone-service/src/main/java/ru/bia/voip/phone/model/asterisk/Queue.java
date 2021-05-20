package ru.bia.voip.phone.model.asterisk;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity(name="queue")
public class Queue {
    @Id
    @Column(name = "name")
    private String name;
    @Column(name = "timeout")
    private Integer timeout;
    @Column(name = "strategy")
    private String strategy;


}
