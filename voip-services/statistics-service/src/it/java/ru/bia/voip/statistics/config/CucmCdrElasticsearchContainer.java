package ru.bia.voip.statistics.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@TestConfiguration
public class CucmCdrElasticsearchContainer extends ElasticsearchContainer {
    private static final String ELASTIC_SEARCH_DOCKER_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:7.10.1";

    public CucmCdrElasticsearchContainer() {
        super(ELASTIC_SEARCH_DOCKER_IMAGE);
        this.addFixedExposedPort(19200, 9200);
        this.addEnv("ELASTIC_PASSWORD", "dev");
        this.addEnv("xpack.security.enabled", "true");
    }

}
