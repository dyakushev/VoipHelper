package ru.bia.voip.statistics.model.voice_portal;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "extension")
public class Extension {
    @Id
    @GeneratedValue
    private Long id;
    private String extension;
    @ManyToOne
    @JoinColumn(name = "extension_type_id", nullable = false)
    private ExtensionType extensionType;
    @OneToMany(mappedBy = "extension")
    private Set<CdrInterval> items;
}
