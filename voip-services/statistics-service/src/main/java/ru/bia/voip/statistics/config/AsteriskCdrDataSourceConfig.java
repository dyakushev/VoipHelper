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
public class AsteriskCdrDataSourceConfig {
    @Value("${app.datasource.asterisk.cdr.username}")
    private String username;
    @Value("${app.datasource.asterisk.cdr.password}")
    private String password;

    // @Primary
    @Bean("asteriskCdrDatasourceProperties")
    @ConfigurationProperties(prefix = "app.datasource.asterisk.cdr")
    public DataSourceProperties asteriskCdrDatasourceProperties() {
        //DataSourceProperties properties = new DataSourceProperties();
        return new DataSourceProperties();
    }

    @Bean("asteriskCdrDatasource")
    @ConfigurationProperties("app.datasource.asterisk.cdr.properties")
    public DataSource asteriskCdrDataSource() {
        return asteriskCdrDatasourceProperties()
                .initializeDataSourceBuilder()
                .username(username)
                .password(password)
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("asteriskSessionFactory")
    public LocalSessionFactoryBean asteriskSessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(asteriskCdrDataSource());
        sessionFactory.setPackagesToScan("ru.bia.voip.statistics.model.asterisk");
        return sessionFactory;
    }

    @Bean(name = "asteriskCdrTransactionManager")
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(asteriskSessionFactory().getObject());
        return transactionManager;
    }
}
