package ru.bia.voip.phone.conifg;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/*@Configuration*/
public class JdbcTemplateConfig {
    private DataSource asteriskDatasource;

    public JdbcTemplateConfig(@Qualifier("asteriskDataSource") DataSource asteriskDatasource) {
        this.asteriskDatasource = asteriskDatasource;
    }

    @Bean
    public JdbcTemplate asteriskJdbcTemplate() {
        return new JdbcTemplate(asteriskDatasource);
    }
}
