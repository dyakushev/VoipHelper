package ru.bia.voip.vc.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class CodecConfig {
    @Value("${cucm.codec.devicepool}")
    private String devicePool;
}
