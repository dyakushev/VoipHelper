package ru.bia.voip.phone.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import ru.bia.voip.phone.conifg.FlywayConfiguration;
import ru.bia.voip.phone.conifg.datasource.impl.AsteriskDataSourceConfiguration;

@TestConfiguration
@Import({AsteriskDataSourceConfiguration.class, FlywayConfiguration.class})
public class TestDataConfig {

}
