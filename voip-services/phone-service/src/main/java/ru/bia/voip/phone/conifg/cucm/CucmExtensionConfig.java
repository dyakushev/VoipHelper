package ru.bia.voip.phone.conifg.cucm;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class CucmExtensionConfig {
    @Value("${cucm.line.partition}")
    private String partition;
}
