package ru.bia.voip.statistics.repo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("it")
@Log4j2
class HdsRepoImplTest {
    @Autowired
    private HdsRepo hdsRepo;

    @Test
    void listDistinctExtensionsBetweenDates() {
   /*     Timestamp from = Timestamp.valueOf(LocalDateTime.now().minusHours(2));
        Timestamp to = Timestamp.valueOf(LocalDateTime.now());
        List<String> extensionList = hdsRepo.listDistinctExtensionsBetweenDates(from, to);
        log.info(extensionList.size());
 */   }
}