package ru.bia.voip.statistics.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
@Profile({"dev", "prod", "it"})
public class HdsDataSourceConfig {
    @Value("${app.datasource.ucce.hds.username}")
    private String username;
    @Value("${app.datasource.ucce.hds.password}")
    private String password;


    @Bean("hdsDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.ucce.hds")
    public DataSourceProperties hdsDatasourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("hdsDatasource")
    @ConfigurationProperties("app.datasource.ucce.hds.properties")
    public DataSource hdsDataSource() {
        return hdsDatasourceProperties()
                .initializeDataSourceBuilder()
                .username(username)
                .password(password)
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("hdsSessionFactory")
    public LocalSessionFactoryBean hdsSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(hdsDataSource());
        sessionFactory.setPackagesToScan("ru.bia.voip.statistics.model.ucce.hds");
        return sessionFactory;
    }

    @Bean(name = "hdsTransactionManager")
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(hdsSessionFactory().getObject());
        return transactionManager;
    }
}
