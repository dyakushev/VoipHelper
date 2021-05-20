package ru.bia.voip.statistics.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Log4j2
class HdsServiceImplTest {
    @Autowired
    private UsageService hdsService;

    @Test
    void listDistinctExtensionsLastMonth() {
    }

    @Test
    void listDistinctExtensionsThisMonth() {
      //  List<String> extList = hdsService.listDistinctExtensionsThisMonth();
        ///log.info(extList.size());
    }

    @Test
    void listDistinctExtensionsBetweenDates() {
    }
}