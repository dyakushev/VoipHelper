package ru.bia.voip.statistics.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;
import ru.bia.voip.statistics.repo.AsteriskCdrRepo;
import ru.bia.voip.statistics.repo.PhoneServiceClient;
import ru.bia.voip.statistics.service.impl.AsteriskCdrServiceImpl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ActiveProfiles("test")
@Log4j2
@ExtendWith(MockitoExtension.class)
class AsteriskCdrServiceImplTest {
    @InjectMocks
    private AsteriskCdrServiceImpl asteriskCdrService;
    @Mock
    private AsteriskCdrRepo asteriskCdrRepo;
    @Mock
    private PhoneServiceClient asteriskExtensionClient;

    private static final Timestamp DATE_FROM = Timestamp.valueOf(LocalDateTime.now());
    private static final Timestamp DATE_TO = Timestamp.valueOf(LocalDateTime.now());
    private static final String CALLING_NUMBER = "MOCK_NUMBER";

    @Test
    void listByCallingNumberAndPeriod_cdrsExist_returnsNonEmptyList() throws IOException {
        //given
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        File responseFile = new ClassPathResource("asterisk_cdr_service/listByCallingNumberAndPeriod.json").getFile();
        List<AsteriskCdr> mockCdrList = mapper.readValue(responseFile, new TypeReference<List<AsteriskCdr>>() {
        });

        //when
        Mockito.doReturn(mockCdrList).when(asteriskCdrRepo).listCdrsByDateAndCallingNumber(any(Timestamp.class), any(Timestamp.class), any(String.class));

        //then
        List<AsteriskCdr> cdrList = asteriskCdrService.listByCallingNumberAndPeriod(CALLING_NUMBER, DATE_FROM, DATE_TO);

        //assert
        assertEquals(mockCdrList, cdrList);
    }

    @Test
    void listByCallingNumberAndPeriod_cdrsDoNotExist_returnsEmptyList() {
        //when
        Mockito.doReturn(Collections.EMPTY_LIST).when(asteriskCdrRepo).listCdrsByDateAndCallingNumber(any(Timestamp.class), any(Timestamp.class), any(String.class));
        //then
        List<AsteriskCdr> cdrList = asteriskCdrService.listByCallingNumberAndPeriod(CALLING_NUMBER, DATE_FROM, DATE_TO);

        //assert
        assertTrue(cdrList.isEmpty());
    }


}