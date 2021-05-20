package ru.bia.voip.phone.conifg.cucm;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;
import ru.bia.voip.phone.exception.jabber.TurnOffSslException;
import ru.bia.voip.phone.util.SSLUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Getter
@Configuration
public class AxlConfig {
    @Value("${cucm.auth.login}")
    private String username;
    @Value("${cucm.auth.password}")
    private String password;
    @Value("${cucm.axl.url}")
    private String axlUrl;
    @Value("${cucm.wsdl.location}")
    private String wsdlLocation;
    @Value("${cucm.wsdl.context-path}")
    private String contextPath;
    @Value("${cucm.axl.namespace}")
    private String nameSpace;

    private class WebServiceMessageSenderWithAuth extends HttpUrlConnectionMessageSender {

        @Override
        protected void prepareConnection(HttpURLConnection connection)
                throws IOException {

            Base64.Encoder enc = Base64.getEncoder();
            String userPassword = username + ":" + password;
            String encodedAuthorization = enc.encodeToString(userPassword.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

            super.prepareConnection(connection);
        }
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(contextPath);
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setDefaultUri(axlUrl);
        template.setMarshaller(marshaller());
        template.setUnmarshaller(marshaller());
        template.setMessageSender(new WebServiceMessageSenderWithAuth());
        try {
            SSLUtil.turnOffSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new TurnOffSslException("Error while turning off ssl", e);
        }
        return template;
    }


}
