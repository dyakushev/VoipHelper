package ru.bia.voip.statistics.config;

import lombok.Getter;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@Getter
@EnableElasticsearchRepositories(basePackages = "ru.bia.voip.statistics.repo.cucm")
public class ElasticSearchConfig {

    @Value("${spring.data.elasticsearch.host}")
    private String elasticSearchHost;

    @Value("${spring.data.elasticsearch.port}")
    private String elasticSearchPort;

    @Value("${spring.data.elasticsearch.user}")
    private String elasticSearchUser;

    @Value("${spring.data.elasticsearch.password}")
    private String elasticSearchPassword;


    @Bean
    public RestHighLevelClient client() {

        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticSearchHost + ":" + elasticSearchPort)
                .withBasicAuth(elasticSearchUser, elasticSearchPassword)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }

}
