package ru.bia.voip.phone.repo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.bia.voip.phone.model.cucm.JabberDevice;
import ru.bia.voip.phone.repo.cucm.JabberRepo;


@Log4j2
@ActiveProfiles("dev")
@SpringBootTest
class LaunchTest {
    @Autowired
    private JabberRepo jabberRepo;

    @Test
    void test() {
        //List<String> jabberList = jabberRepo.listJabberName();
        JabberDevice jabberDevice = jabberRepo.getJabberDevice("FSAKAYZEROVA");
        log.info(jabberDevice.toString());

    }
}
