package ru.bia.voip.phone.conifg.datasource.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.bia.voip.phone.conifg.datasource.DatasourceConfig;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "ru.bia.voip.phone.repo.asterisk",
        entityManagerFactoryRef = "asteriskEntityManagerFactory",
        transactionManagerRef = "asteriskTransactionManager"
)
public class AsteriskDataSourceConfiguration implements DatasourceConfig {
    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.asterisk")
    public DataSourceProperties asteriskDatasourceProperties() {
        return new DataSourceProperties();
    }


    @Primary
    @Bean
    @ConfigurationProperties("app.datasource.asterisk.configuration")
    public DataSource asteriskDataSource() {
        return asteriskDatasourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }


    @Primary
    @Bean("asteriskEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean asteriskEntityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(asteriskDataSource())
                .packages("ru.bia.voip.phone.model.asterisk")
                .build();
    }

    @Primary
    @Bean("asteriskTransactionManager")
    public PlatformTransactionManager asteriskTransactionManager(final LocalContainerEntityManagerFactoryBean asteriskEntityManagerFactory) {
        return new JpaTransactionManager(asteriskEntityManagerFactory.getObject());
    }

}
