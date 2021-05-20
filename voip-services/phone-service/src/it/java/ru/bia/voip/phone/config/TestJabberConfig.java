package ru.bia.voip.phone.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import ru.bia.voip.phone.conifg.cucm.AxlConfig;
import ru.bia.voip.phone.conifg.cucm.JabberConfig;
import ru.bia.voip.phone.repo.cucm.impl.JabberRepoImpl;

@TestConfiguration

@Import({AxlConfig.class, JabberConfig.class, JabberRepoImpl.class})
public class TestJabberConfig {
}
