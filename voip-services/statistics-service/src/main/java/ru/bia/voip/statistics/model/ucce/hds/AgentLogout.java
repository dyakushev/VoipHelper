package ru.bia.voip.statistics.model.ucce.hds;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "t_Agent_Logout")
public class AgentLogout {
    @Id
    @Column(name = "LogoutDateTime")
    private Timestamp logoutDateTime;
    @Column(name = "Extension")
    private String extension;

}
