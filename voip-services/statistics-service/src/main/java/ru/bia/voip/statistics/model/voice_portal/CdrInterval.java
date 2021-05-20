package ru.bia.voip.statistics.model.voice_portal;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "cdr_interval")
public class CdrInterval {
    @Id
    private Timestamp dateTime;
    @ManyToOne
    @JoinColumn(name = "extension_id", nullable = false)
    private Extension extension;
    private Long talkTime;
    private Long callsCount;

}
