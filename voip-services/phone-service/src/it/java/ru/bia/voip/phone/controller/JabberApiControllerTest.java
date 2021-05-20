package ru.bia.voip.phone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.bia.voip.phone.model.cucm.rest.JabberRequest;
import ru.bia.voip.phone.service.JabberService;

import javax.ws.rs.core.MediaType;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(JabberApiController.class)
@ActiveProfiles("test")
@Log4j2
class JabberApiControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JabberService jabberService;
    private static final String USER_NAME = "mockuser", EXTENSION = "12345";

    @Test
    void addJabber_jabberCreated_returnsJsonWithDirNumber()
            throws Exception {
        JabberRequest jabberRequest = JabberRequest.builder().username(USER_NAME).build();
        given(jabberService.addJabber(USER_NAME)).willReturn(EXTENSION);
        String jsonJabberRequest = objectMapper.writeValueAsString(jabberRequest);
        mvc.perform(post("/api/v1/jabber_cc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonJabberRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.extension").value(EXTENSION));
    }
}