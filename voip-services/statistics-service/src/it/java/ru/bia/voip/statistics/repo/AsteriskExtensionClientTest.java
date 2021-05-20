package ru.bia.voip.statistics.repo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bia.voip.statistics.config.AsteriskExtensionClientTestConfig;
import ru.bia.voip.statistics.model.asterisk.AsteriskExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("it")
@Log4j2
@SpringBootTest(
        classes = AsteriskExtensionClientTestConfig.FeignConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class AsteriskExtensionClientTest {
    @Autowired
    private PhoneServiceClient asteriskExtensionClient;
    private static final String ID = "100715";
    private static final String EXTEN = "69315";
    private static final String FAKE_ID = "123456";

    @Test
    void getExtenById_extensionExists_ReturnsExtensionWithId() {
        List<AsteriskExtension> extensionList = asteriskExtensionClient.getExtenById(ID);
        assertEquals(1, extensionList.size());
        String exten = extensionList.get(0).getExten();
        assertEquals(EXTEN, exten);

    }

    @Test
    void getExtenById_extensionDoesNotExitst_ReturnsEmptyList() {
        List<AsteriskExtension> extensionList = asteriskExtensionClient.getExtenById(FAKE_ID);
        assertEquals(0, extensionList.size());
    }
}
