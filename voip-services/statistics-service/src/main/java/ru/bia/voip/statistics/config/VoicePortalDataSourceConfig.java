package ru.bia.voip.statistics.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile({"dev", "prod", "it"})
public class VoicePortalDataSourceConfig {
    @Value("${app.datasource.voice-portal.username}")
    private String username;
    @Value("${app.datasource.voice-portal.password}")
    private String password;


    @Bean("voicePortalDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.voice-portal")
    public DataSourceProperties voicePortalDatasourceProperties() {
        //DataSourceProperties dataSourceProperties = new DataSourceProperties();

        return new DataSourceProperties();
    }

    @FlywayDataSource
    @Bean("voicePortalDatasource")
    @ConfigurationProperties("app.datasource.voice-portal.properties")
    public DataSource voicePortalDataSource() {
        return voicePortalDatasourceProperties()
                .initializeDataSourceBuilder()
                .username(username)
                .password(password)
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("voicePortalSessionFactory")
    public LocalSessionFactoryBean voicePortalSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        Properties hibernateProperties = new Properties();
        // hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
        sessionFactory.setDataSource(voicePortalDataSource());
        sessionFactory.setHibernateProperties(hibernateProperties);
        sessionFactory.setPackagesToScan("ru.bia.voip.statistics.model.voice_portal");
        return sessionFactory;
    }

    @Bean(name = "voicePortalTransactionManager")
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(voicePortalSessionFactory().getObject());
        return transactionManager;
    }
}
