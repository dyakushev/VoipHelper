package ru.bia.voip.phone.conifg;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Profile({"test"})
//@Configuration
public class FlywayConfiguration {

    private DataSource asteriskDataSource;


    public FlywayConfiguration(@Qualifier("asteriskDataSource") DataSource asteriskDataSource) {
        this.asteriskDataSource = asteriskDataSource;
    }

    @PostConstruct
    public void migrateFlyway() {
        Flyway flyway = Flyway
                .configure()
                .dataSource(asteriskDataSource)
                .locations("db/migration_asterisk")
                .load();
        flyway.migrate();

    }
}
