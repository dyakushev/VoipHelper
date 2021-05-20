package ru.bia.voip.phone.repo.cucm.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.webservices.client.AutoConfigureMockWebServiceServer;
import org.springframework.boot.test.autoconfigure.webservices.client.AutoConfigureWebServiceClient;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.test.client.MockWebServiceServer;
import ru.bia.voip.phone.config.TestJabberConfig;
import ru.bia.voip.phone.exception.UserNameDoesNotExistException;
import ru.bia.voip.phone.exception.jabber.JabberAssociateException;
import ru.bia.voip.phone.exception.jabber.JabberCreateException;
import ru.bia.voip.phone.exception.jabber.JabberDoesNotExist;
import ru.bia.voip.phone.exception.jabber.JabberUpdateException;
import ru.bia.voip.phone.repo.cucm.JabberRepo;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;

import static org.springframework.ws.test.client.RequestMatchers.payload;
import static org.springframework.ws.test.client.ResponseCreators.withPayload;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestJabberConfig.class)
@WebServiceClientTest(excludeAutoConfiguration = {AutoConfigureWebServiceClient.class, AutoConfigureCache.class, AutoConfigureMockWebServiceServer.class})
class JabberRepoImplTest {
    @Autowired
    private JabberRepo jabberRepo;
    @Autowired
    private WebServiceTemplate webServiceTemplate;

    private MockWebServiceServer mockWebServiceServer;
    private static final String USER_NAME = "mockuser",
            DEVICE_NAME = "CSFMOCKUSER",
            DIR_NUMBER = "12345";


    @BeforeEach
    void setUp() {
        mockWebServiceServer = MockWebServiceServer.createServer(webServiceTemplate);
    }

    @Test
    void usernameExists_userExists_returnsTrue()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/getUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getUserRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);

        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));

        boolean userExist = jabberRepo.usernameExists(USER_NAME);
        Assertions.assertTrue(userExist);
        mockWebServiceServer.verify();
    }

    @Test
    void usernameExist_userDoesNotExist_throwsException()
            throws IOException {
        File requestFile = new ClassPathResource("cucm/jabber/getUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getUserException.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));

        Assertions.assertThrows(UserNameDoesNotExistException.class, () -> jabberRepo.usernameExists(USER_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void createJabber_noError_creationSuccessful()
            throws IOException {
        File requestFile = new ClassPathResource("cucm/jabber/addPhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/addPhoneRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);

        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        jabberRepo.createJabber(DEVICE_NAME, USER_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void createJabber_deviceWithNameAlreadyExist_ThrowsException()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/addPhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/addPhoneException.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        Assertions.assertThrows(JabberCreateException.class, () -> jabberRepo.createJabber(DEVICE_NAME, USER_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void updateUser_userExists_updateSuccessful()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/updateUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/updateUserRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile); //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        jabberRepo.updateUser(USER_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void updateUser_userDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/updateUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/updateUserException1.xml").getFile();
        Source responsePayload = new StreamSource(responseFile); //when

        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        Assertions.assertThrows(JabberUpdateException.class, () -> jabberRepo.updateUser(USER_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateLineAndJabber_deviceAndLineExist_associationSuccessful()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/updatePhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/updatePhoneRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile); //when
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        jabberRepo.associateLineAndJabber(DIR_NUMBER, DEVICE_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void associateLineAndJabber_deviceDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/updatePhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/updatePhoneException1.xml").getFile();
        Source responsePayload = new StreamSource(responseFile); //when
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateLineAndJabber(DIR_NUMBER, DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateLineAndJabber_lineDoesNotExist_throwsException()
            throws IOException {
        File requestFile = new ClassPathResource("cucm/jabber/updatePhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/updatePhoneException2.xml").getFile();
        Source responsePayload = new StreamSource(responseFile); //when
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateLineAndJabber(DIR_NUMBER, DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateAppUserAndJabber_userAndDeviceExist_associationSuccessful()
            throws IOException {
        //given
        File requestFile1 = new ClassPathResource("cucm/jabber/getAppUserReq.xml").getFile();
        Source expectedRequestPayloadR1 = new StreamSource(requestFile1);
        File responseFile1 = new ClassPathResource("cucm/jabber/getAppUserRes.xml").getFile();
        Source responsePayloadR1 = new StreamSource(responseFile1);


        File requestFile2 = new ClassPathResource("cucm/jabber/updateAppUserReq.xml").getFile();
        Source expectedRequestPayloadR2 = new StreamSource(requestFile2);
        File responseFile2 = new ClassPathResource("cucm/jabber/updateAppUserRes.xml").getFile();
        Source responsePayloadR2 = new StreamSource(responseFile2);

        //when
        mockWebServiceServer.expect(payload(expectedRequestPayloadR1)).andRespond(withPayload(responsePayloadR1));
        mockWebServiceServer.expect(payload(expectedRequestPayloadR2)).andRespond(withPayload(responsePayloadR2));
        jabberRepo.associateAppUserAndJabber(DEVICE_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void associateAppUserAndJabber_userDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/getAppUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getAppUserException.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);       //when
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateAppUserAndJabber(DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateAppUserAndJabber_deviceDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile1 = new ClassPathResource("cucm/jabber/getAppUserReq.xml").getFile();
        Source expectedRequestPayloadR1 = new StreamSource(requestFile1);
        File responseFile1 = new ClassPathResource("cucm/jabber/getAppUserRes.xml").getFile();
        Source responsePayloadR1 = new StreamSource(responseFile1);

        File requestFile2 = new ClassPathResource("cucm/jabber/updateAppUserReq.xml").getFile();
        Source expectedRequestPayloadR2 = new StreamSource(requestFile2);
        File responseFile2 = new ClassPathResource("cucm/jabber/updateAppUserException.xml").getFile();
        Source responsePayloadR2 = new StreamSource(responseFile2);
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayloadR1)).andRespond(withPayload(responsePayloadR1));
        mockWebServiceServer.expect(payload(expectedRequestPayloadR2)).andRespond(withPayload(responsePayloadR2));
        //assert
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateAppUserAndJabber(DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateEndUserAndJabber_userAndDeviceExist_associationSuccessful()
            throws IOException {
        //given
        File requestFile1 = new ClassPathResource("cucm/jabber/getUserReq.xml").getFile();
        Source expectedRequestPayloadR1 = new StreamSource(requestFile1);
        File responseFile1 = new ClassPathResource("cucm/jabber/getUserRes.xml").getFile();
        Source responsePayloadR1 = new StreamSource(responseFile1);

        File requestFile2 = new ClassPathResource("cucm/jabber/updateUserReq2.xml").getFile();
        Source expectedRequestPayloadR2 = new StreamSource(requestFile2);
        File responseFile2 = new ClassPathResource("cucm/jabber/updateUserRes.xml").getFile();
        Source responsePayloadR2 = new StreamSource(responseFile2);


        //when
        mockWebServiceServer.expect(payload(expectedRequestPayloadR1)).andRespond(withPayload(responsePayloadR1));
        mockWebServiceServer.expect(payload(expectedRequestPayloadR2)).andRespond(withPayload(responsePayloadR2));
        //assert

        jabberRepo.associateEndUserAndJabber(USER_NAME, DEVICE_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void associateEndUserAndJabber_userDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/getUserReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getUserException.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);     //when
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateEndUserAndJabber(USER_NAME, DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void associateEndUserAndJabber_deviceDoesNotExist_throwsException()
            throws IOException {
        //given
        File requestFile1 = new ClassPathResource("cucm/jabber/getUserReq.xml").getFile();
        Source expectedRequestPayloadR1 = new StreamSource(requestFile1);
        File responseFile1 = new ClassPathResource("cucm/jabber/getUserRes.xml").getFile();
        Source responsePayloadR1 = new StreamSource(responseFile1);

        File requestFile2 = new ClassPathResource("cucm/jabber/updateUserReq2.xml").getFile();
        Source expectedRequestPayloadR2 = new StreamSource(requestFile2);
        File responseFile2 = new ClassPathResource("cucm/jabber/updateUserException2.xml").getFile();
        Source responsePayloadR2 = new StreamSource(responseFile2);

        //when
        mockWebServiceServer.expect(payload(expectedRequestPayloadR1)).andRespond(withPayload(responsePayloadR1));
        mockWebServiceServer.expect(payload(expectedRequestPayloadR2)).andRespond(withPayload(responsePayloadR2));
        //assert
        Assertions.assertThrows(JabberAssociateException.class, () -> jabberRepo.associateEndUserAndJabber(USER_NAME, DEVICE_NAME));
        mockWebServiceServer.verify();
    }

    @Test
    void listJabberName_devicesExist_returnsNonEmptyList() throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/listPhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/listPhoneRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        jabberRepo.listJabberName();
        mockWebServiceServer.verify();
    }

    @Test
    void getJabberDevice_deviceExists_returnsJabberDevice() throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/getPhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getPhoneRes.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        jabberRepo.getJabberDevice(DEVICE_NAME);
        mockWebServiceServer.verify();
    }

    @Test
    void getJabberDevice_deviceDoesNotExist_ThrowsException() throws IOException {
        //given
        File requestFile = new ClassPathResource("cucm/jabber/getPhoneReq.xml").getFile();
        Source expectedRequestPayload = new StreamSource(requestFile);
        File responseFile = new ClassPathResource("cucm/jabber/getPhoneException.xml").getFile();
        Source responsePayload = new StreamSource(responseFile);
        //when
        mockWebServiceServer.expect(payload(expectedRequestPayload)).andRespond(withPayload(responsePayload));
        //assert
        Assertions.assertThrows(JabberDoesNotExist.class, () -> jabberRepo.getJabberDevice(DEVICE_NAME));
        mockWebServiceServer.verify();

    }
/*    @Test
    void associateAppUserAndJabber(){
        jabberRepo.associateAppUserAndJabber("CSFYDOADM");
    }*/

}