package ru.bia.voip.statistics.repo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Log4j2
@ActiveProfiles("it")
@SqlGroup({
        @Sql(scripts = "classpath:sql/cdr_schema.sql",
                config = @SqlConfig(dataSource = "asteriskCdrDatasource", transactionManager = "asteriskCdrTransactionManager")),
        @Sql(scripts = "classpath:sql/import_cdrs.sql",
                config = @SqlConfig(dataSource = "asteriskCdrDatasource", transactionManager = "asteriskCdrTransactionManager")
        )}
)
class AsteriskCdrRepoImplTest {
    @Autowired
    private AsteriskCdrRepo asteriskCdrRepo;


    @Test
    void listCdrsByDateAndCallingNumber_cdrsExist_returnsNonEmptyList() {
        //given
        final String CALLING_NUMBER = "60000";
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 11, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2020, 11, 01, 0, 0));
        //when
        List<AsteriskCdr> cdrList = asteriskCdrRepo.listCdrsByDateAndCallingNumber(DATE_FROM, DATE_TO, CALLING_NUMBER);
        //assert
        assertEquals(1, cdrList.size());
    }

    @Test
    void listCdrsByDateAndCallingNumber_cdrsDoNotExist_returnsEmptyList() {
        //given
        final String CALLING_NUMBER = "60000";
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2021, 12, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2021, 12, 1, 0, 0));
        //when
        List<AsteriskCdr> cdrList = asteriskCdrRepo.listCdrsByDateAndCallingNumber(DATE_FROM, DATE_TO, CALLING_NUMBER);
        //assert
        assertEquals(0, cdrList.size());
    }


    @Test
    void listCallingNumbersByDate_cdrsExist_returnsNonEmptyList() {
        //given
        final String CALLING_NUMBER = "100060000";
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 11, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2020, 11, 1, 0, 0));
        //when
        List<String> cdrList = asteriskCdrRepo.listCallingNumbersByDate(DATE_FROM, DATE_TO);
        //assert
        assertEquals(1, cdrList.size());
        assertEquals(CALLING_NUMBER, cdrList.get(0));
    }

    @Test
    void listCallingNumbersByDate_cdrsDoNotExist_returnsEmptyList() {
        //given
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2022, 11, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2022, 11, 1, 0, 0));
        //when
        List<String> cdrList = asteriskCdrRepo.listCallingNumbersByDate(DATE_FROM, DATE_TO);
        //assert
        assertEquals(0, cdrList.size());
    }


    @Test
    void listCalledNumbersByDate_cdrsExist_returnsNonEmptyList() {
        //given
        final String CALLED_NUMBER = "69999";
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 11, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2020, 11, 1, 0, 0));
        //when
        List<String> cdrList = asteriskCdrRepo.listCalledNumbersByDate(DATE_FROM, DATE_TO);
        //assert
        assertEquals(1, cdrList.size());
        assertEquals(CALLED_NUMBER, cdrList.get(0));
    }

    @Test
    void listCalledNumbersByDate_cdrsDoNotExist_returnsEmptyList() {
        //given
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2022, 11, 23, 23, 59));
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2022, 11, 1, 0, 0));
        //when
        List<String> cdrList = asteriskCdrRepo.listCalledNumbersByDate(DATE_FROM, DATE_TO);
        //assert
        assertEquals(0, cdrList.size());
    }


    @Test
    void mapExtensionToNumberOfIncomingCallsByDateAndExtensionList_cdrsExist_returnsNonEmptyMap() {
        //given
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2019, 1, 1, 00, 00));
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 12, 31, 23, 59));
        final String CALLED_NUMBER = "69999";
        final List<String> DST_LIST = Collections.singletonList(CALLED_NUMBER);
        //when
        Map<String, Long> map = asteriskCdrRepo.mapExtensionToNumberOfIncomingCallsByDate(DATE_FROM, DATE_TO, DST_LIST);
        //assert
        assertEquals(1, map.size());
        assertEquals(2L, map.get(CALLED_NUMBER));
    }

    @Test
    void mapExtensionToNumberOfIncomingCallsByDateAndExtensionList_cdrsDoNotExist_returnsEmptyMap() {
        //given
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2021, 1, 1, 00, 00));
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2021, 12, 31, 23, 59));
        final String CALLED_NUMBER = "69998";
        final List<String> DST_LIST = Collections.singletonList(CALLED_NUMBER);
        //when
        Map<String, Long> map = asteriskCdrRepo.mapExtensionToNumberOfIncomingCallsByDate(DATE_FROM, DATE_TO, DST_LIST);
        //assert
        assertEquals(0, map.size());
    }


    @Test
    void mapExtensionToNumberOfOutgoingCallsByDateAndExtensionList_cdrsExist_returnsNonEmptyMap() {
        //given
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2019, 1, 1, 00, 00));
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 12, 31, 23, 59));
        final String CALLING_NUMBER = "100060000";
        final List<String> DST_LIST = Collections.singletonList(CALLING_NUMBER);
        //when
        Map<String, Long> map = asteriskCdrRepo.mapExtensionToNumberOfOutgoingCallsByDate(DATE_FROM, DATE_TO, DST_LIST);
        //assert
        assertEquals(1, map.size());
        assertEquals(2L, map.get(CALLING_NUMBER));
    }

    @Test
    void mapExtensionToNumberOfOutgoingCallsByDateAndExtensionList_cdrsDoNotExist_returnsEmptyMap() {
        //given
        final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.of(2019, 1, 1, 00, 00));
        final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.of(2020, 12, 31, 23, 59));
        final String CALLING_NUMBER = "100060001";
        final List<String> DST_LIST = Collections.singletonList(CALLING_NUMBER);
        //when
        Map<String, Long> map = asteriskCdrRepo.mapExtensionToNumberOfOutgoingCallsByDate(DATE_FROM, DATE_TO, DST_LIST);
        //assert
        assertEquals(0, map.size());

    }
}