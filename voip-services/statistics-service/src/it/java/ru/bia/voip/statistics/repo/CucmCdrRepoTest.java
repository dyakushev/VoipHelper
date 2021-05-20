package ru.bia.voip.statistics.repo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.bia.voip.statistics.config.CucmCdrElasticsearchContainer;
import ru.bia.voip.statistics.model.cucm.CucmCdr;
import ru.bia.voip.statistics.repo.cucm.CucmCdrRepoOperations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@Log4j2
@ActiveProfiles("it")
class CucmCdrRepoTest {
    @Container
    private static final ElasticsearchContainer elasticsearchContainer = new CucmCdrElasticsearchContainer();

    @Autowired
    private ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    private CucmCdrRepoOperations cucmCdrRepoOperations;


    private static final String FROM = "12345";
    private static final String FAKE_FROM = "54321";
    private static final String FAKE_TO = "000000000";
    private static final String TO = "9999999999";
    private static final LocalDateTime DATE_FROM = LocalDateTime.now().minusMinutes(20L);
    private static final LocalDateTime DATE_BETWEEN_TO_AND_FROM = LocalDateTime.now().minusMinutes(10L);
    private static final LocalDateTime DATE_TO = LocalDateTime.now();
    private static final String INDEX = "cdr-1";

    @BeforeAll
    static void setUp() {
        elasticsearchContainer.start();
    }

    @BeforeEach
    public void init() {
        boolean indexExists = elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX)).exists();
        if (indexExists)
            elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX)).delete();
        elasticsearchTemplate.indexOps(IndexCoordinates.of(INDEX)).create();
        fillData();

    }

    private void fillData() {
        elasticsearchTemplate.save(CucmCdr.builder()
                .from(FROM)
                .to(TO)
                .timeStamp(DATE_BETWEEN_TO_AND_FROM)
                .build(), IndexCoordinates.of(INDEX));
    }

    @AfterAll
    static void tearDown() {
        elasticsearchContainer.stop();
    }

    @Test
    @Order(1)
    void checkDockerContainer_containerWorksFine_returnsTrue() {
        assertEquals(Boolean.TRUE, elasticsearchContainer.isRunning());
    }

    @Test
    @Order(2)
    void countCucmCdrByFromAndTimeStampBetween_cdrsExist_returnsMoreThanZero() {

        //  Long count = cucmCdrRepo.countCucmCdrByFromAndTimeStampBetween(FROM, DATE_FROM, DATE_TO);
        Long count = cucmCdrRepoOperations.countCucmCdrByFromAndTimeStampBetween(FROM, DATE_FROM, DATE_TO);
        assertEquals(1L, count);
    }

    @Test
    @Order(3)
    void countCucmCdrByFromAndTimeStampBetween_cdrsDoNotExist_returnsZero() {
        //  Long count = cucmCdrRepo.countCucmCdrByFromAndTimeStampBetween(FROM, DATE_FROM, DATE_TO);
        Long count = cucmCdrRepoOperations.countCucmCdrByFromAndTimeStampBetween(FAKE_FROM, DATE_FROM, DATE_TO);
        assertEquals(0L, count);
    }

    @Test
    @Order(4)
    void countCucmCdrByToAndTimeStampBetween_cdrsExist_returnsMoreThanZero() {
        Long count = cucmCdrRepoOperations.countCucmCdrByToAndTimeStampBetween(TO, DATE_FROM, DATE_TO);
        assertEquals(1L, count);
    }

    @Test
    @Order(5)
    void countCucmCdrByToAndTimeStampBetween_cdrsDoNotExist_returnsZero() {
        //  Long count = cucmCdrRepo.countCucmCdrByFromAndTimeStampBetween(FROM, DATE_FROM, DATE_TO);
        Long count = cucmCdrRepoOperations.countCucmCdrByToAndTimeStampBetween(FAKE_TO, DATE_FROM, DATE_TO);
        assertEquals(0L, count);
    }
}