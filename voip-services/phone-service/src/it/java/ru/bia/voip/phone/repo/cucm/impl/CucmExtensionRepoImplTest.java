package ru.bia.voip.phone.repo.cucm.impl;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import ru.bia.voip.phone.repo.cucm.CucmExtensionRepo;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Log4j2
//@WebServiceClientTest(excludeAutoConfiguration = {AutoConfigureWebServiceClient.class, AutoConfigureCache.class, AutoConfigureMockWebServiceServer.class})
class CucmExtensionRepoImplTest {
    @Autowired
    private CucmExtensionRepo cucmExtensionRepo;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    private MockWebServiceServer mockWebServiceServer;

    private static final String DIR_NUMBER = "12345";

    @BeforeEach
    void setUp() {
        mockWebServiceServer = MockWebServiceServer.createServer(webServiceTemplate);
    }

    @Test
    void lineExists() {
        boolean lineExists = cucmExtensionRepo.lineExists(DIR_NUMBER);

        log.info(lineExists);
    }

    @Test
    void createLine() {
    }

    @Test
    void getDevicesByDirNumber() {
    }

    @Test
    void updateDeviceWithDirNumber() {
    }
}