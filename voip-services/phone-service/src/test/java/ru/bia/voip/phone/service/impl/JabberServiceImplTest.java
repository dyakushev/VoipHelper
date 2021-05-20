package ru.bia.voip.phone.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bia.voip.phone.exception.UserNameDoesNotExistException;
import ru.bia.voip.phone.repo.cucm.JabberRepo;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class JabberServiceImplTest {
    @Mock
    private JabberRepo jabberRepo;
    @InjectMocks
    private JabberServiceImpl jabberService;
    private static final String USER_NAME = "mockuser",
            DIR_NUMBER = "12345",
            DEVICE_NAME = "CSFMOCKUSER",
            APP_USER_NAME = "mockappuser";

    @Test
    void addJabber_noException_createsJabber() {
        //given
        Mockito.doReturn(Boolean.TRUE).when(jabberRepo).usernameExists(USER_NAME);
        Mockito.doNothing().when(jabberRepo).updateUser(USER_NAME);
        Mockito.doReturn(Optional.of(DIR_NUMBER)).when(jabberRepo).getNextFreeDirNumber();
        Mockito.doNothing().when(jabberRepo).createLine(DIR_NUMBER);
        Mockito.doReturn(Optional.of(DEVICE_NAME)).when(jabberRepo).getDeviceNameFromUserName(USER_NAME);
        Mockito.doNothing().when(jabberRepo).createJabber(DEVICE_NAME, USER_NAME);
        Mockito.doNothing().when(jabberRepo).associateLineAndJabber(DIR_NUMBER, DEVICE_NAME);
        Mockito.doNothing().when(jabberRepo).associateEndUserAndJabber(USER_NAME, DEVICE_NAME);
        // Mockito.doNothing().when(jabberRepo).associateAppUserAndJabber(APP_USER_NAME, DEVICE_NAME);
        //when
        String dirNumber = jabberService.addJabber(USER_NAME);

        //assert
        Assertions.assertEquals(DIR_NUMBER, dirNumber);
    }

    @Test
    void addJabber_userDoesNotExist_throwsException() {
        //given
        Mockito.when(jabberRepo.usernameExists(USER_NAME)).thenReturn(Boolean.FALSE);
        //assert
        Assertions.assertThrows(UserNameDoesNotExistException.class, () -> jabberService.addJabber(USER_NAME));
    }

}