package ru.bia.voip.phone.repo;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.TestDatabaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.bia.voip.phone.config.TestDataConfig;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;
import ru.bia.voip.phone.repo.asterisk.AsteriskExtensionRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {FlywayAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, TestDatabaseAutoConfiguration.class})
@Import(TestDataConfig.class)
public class AsteriskExtensionRepoTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private AsteriskExtensionRepo asteriskExtensionRepo;

    private AsteriskExtension extension;
    private final static Integer ID_10000 = 10000, ID_10001 = 10001;
    private final static String EXTEN_10000 = "10000", EXTEN_10001 = "10001";


    @Test
    void findAsteriskExtensionsByExten_ExtensionExists_ReturnsExtension() {
        //given
        extension = new AsteriskExtension();
        extension.setId(ID_10000);
        extension.setExten(EXTEN_10000);
        testEntityManager.persist(extension);
        testEntityManager.flush();
        //when
        List<AsteriskExtension> foundExtensionList = asteriskExtensionRepo.findAsteriskExtensionsByExten(extension.getExten(), Pageable.unpaged()).stream().collect(Collectors.toList());
        AsteriskExtension foundExtension = foundExtensionList.get(0);

        //assert
        assertTrue(foundExtensionList.size() == 1);
        assertEquals(extension.getExten(), foundExtension.getExten());
    }


    @Test
    void updateAsteriskExtensionIdByExten_ExtensionsWithIdExists_ReturnsInt() {
        //given
        this.extension = new AsteriskExtension();
        this.extension.setId(ID_10000);
        this.extension.setExten(EXTEN_10001);
        testEntityManager.persist(this.extension);
        testEntityManager.flush();
        int result = asteriskExtensionRepo.updateExtensionIdByExten(ID_10001, EXTEN_10001);

        //assert
        assertEquals(1, result);
        Optional<AsteriskExtension> asteriskExtensionOptional = asteriskExtensionRepo.findById(ID_10001);
        assertTrue(asteriskExtensionOptional.isPresent());
        AsteriskExtension extension = asteriskExtensionOptional.get();
        assertEquals(EXTEN_10001, extension.getExten());
    }
}
