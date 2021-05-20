package ru.bia.voip.statistics.model.voice_portal;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "extension_type")
public class ExtensionType {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(mappedBy = "extensionType")
    private Set<Extension> extensions;
}
