package ru.bia.voip.statistics.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.bia.voip.statistics.model.asterisk.AsteriskExtension;
import ru.bia.voip.statistics.repo.PhoneServiceClient;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Log4j2
@TestConfiguration
public class AsteriskExtensionClientTestConfig {
    @Configuration
    public static class RibbonConfig {

        @LocalServerPort
        int port;

        @Bean
        public ServerList<Server> serverList() {
            return new StaticServerList<>(new Server("127.0.0.1", port));
        }
    }

    @EnableFeignClients(clients = PhoneServiceClient.class)
    @RestController
    @Configuration
    @EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
    @RibbonClient(name = "phone-service", configuration = AsteriskExtensionClientTestConfig.RibbonConfig.class)
    public static class FeignConfig {
        @RequestMapping(method = RequestMethod.GET, value = "/api/v1/asterisk/extensions/id/{id}", consumes = "application/json")
        List<AsteriskExtension> getExtenById(@PathVariable("id") String id) {
            ObjectMapper mapper = new ObjectMapper();
            //AsteriskException does not contain all the fields, we should ignore some
            //of them from reading
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            try {
                File responseFile = new ClassPathResource("extension_client/getExtenById_" + id + ".json").getFile();
                List<AsteriskExtension> extensionList = mapper.readValue(responseFile, new TypeReference<List<AsteriskExtension>>() {
                });
                return extensionList;
            } catch (IOException e) {
                log.error(e.getMessage());
                return Collections.emptyList();
            }
        }
    }
}
