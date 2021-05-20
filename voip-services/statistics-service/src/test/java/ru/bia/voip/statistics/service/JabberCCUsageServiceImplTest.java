package ru.bia.voip.statistics.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.bia.voip.statistics.service.impl.JabberCCUsageServiceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@Log4j2
class JabberCCUsageServiceImplTest {
    @Autowired
    private JabberCCUsageServiceImpl jabberCCUsageService;

  //  @Test
    void listUnusedDevicesThisMonth() {
        LocalDateTime thisMonth = LocalDateTime.now().minusYears(1L);
        Timestamp from = Timestamp.valueOf(thisMonth
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
        );
        Timestamp to = Timestamp.valueOf(thisMonth);
        jabberCCUsageService.listUnusedDevicesBetweenDates(from,to).forEach(s -> log.info(s));
    }
}