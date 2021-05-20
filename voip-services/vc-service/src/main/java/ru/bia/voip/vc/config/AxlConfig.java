package ru.bia.voip.vc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import ru.bia.voip.cucm.wsdl.v10_5.AXLAPIService;
import ru.bia.voip.cucm.wsdl.v10_5.AXLPort;
import ru.bia.voip.vc.util.RandomPasswordGenerator;
import ru.bia.voip.vc.util.SSLUtil;

import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

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


    @Bean
    @Lazy
    public AXLPort axlPort() {
        URL url = getClass().getClassLoader().getResource(wsdlLocation);
        AXLAPIService service = new AXLAPIService(url);
        AXLPort axlPort = service.getAXLPort();
        BindingProvider prov = (BindingProvider) axlPort;
        prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
        prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, axlUrl);
        try {
            SSLUtil.turnOffSslChecking();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
        }
        return axlPort;
    }


    @Bean
    public RandomPasswordGenerator randomPasswordGenerator() {
        return new RandomPasswordGenerator();
    }


}
