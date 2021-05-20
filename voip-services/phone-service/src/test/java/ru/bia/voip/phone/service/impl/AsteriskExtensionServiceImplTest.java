package ru.bia.voip.phone.service.impl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.bia.voip.phone.exception.TwoOrMoreExtensionsOrPhonesFound;
import ru.bia.voip.phone.exception.ZeroExtensionsOrPhonesFound;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;

import ru.bia.voip.phone.model.asterisk.AsteriskExtensionType;
import ru.bia.voip.phone.repo.asterisk.AsteriskExtensionRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class AsteriskExtensionServiceImplTest {
    @Mock
    private AsteriskExtensionRepo asteriskExtensionRepo;
    @InjectMocks
    private AsteriskExtensionServiceImpl asteriskExtensionServiceImpl;

    private AsteriskExtension fromExtension;
    private String toExtensionString, fromExtensionString;
    private Integer extensionId;
    private AsteriskExtension toExtension;
    private AtomicInteger count;


    private final static String
            EXTENSION_61111 = "61111",
            EXTENSION_60000 = "60000",
            APPDATA_61111 = "61111",
            APPDATA_60000 = "60000";
    private static final Integer
            ID_61111 = 61111,
            ID_60000 = 60000;


    @BeforeEach
    void init() {
        //Mockito.mockitoSession().initMocks(this);
        fromExtension = new AsteriskExtension();
        toExtensionString = "";
        fromExtensionString = "";
        toExtension = new AsteriskExtension();


        fromExtension.setId(ID_60000);
        fromExtension.setExten(EXTENSION_61111);
        fromExtension.setAppdata(EXTENSION_61111);


        toExtensionString = EXTENSION_60000;
        fromExtensionString = EXTENSION_61111;

        count = new AtomicInteger(0);
    }


    @Test
    void changeExtension_ToExtensionDoesNotExist_ExtensionChanged() {
        //fiven
        toExtension.setId(ID_60000);
        toExtension.setExten(EXTENSION_60000);
        toExtension.setAppdata(APPDATA_61111);

        //when
        Mockito.when(asteriskExtensionRepo.findAsteriskExtensionsByExten(toExtensionString, Pageable.unpaged())).thenAnswer(answer -> {

                    if (count.get() == 0) {
                        count.incrementAndGet();
                        return Page.empty();
                    }
                    return new PageImpl<>(Collections.singletonList(toExtension));
                }
        );
        Mockito.when(asteriskExtensionRepo.save(fromExtension)).thenReturn(fromExtension);

        //then
        AsteriskExtension extension = (AsteriskExtension) asteriskExtensionServiceImpl.changeExtension(fromExtension, toExtensionString);

        //assert
        assertEquals(extension.getExten(), toExtensionString);
        assertEquals(extension.getId(), Integer.valueOf(toExtensionString));
    }

    @Test
    void changeExtension_ToExtensionExists_ExtensionChanged() {
        //given
        toExtension.setId(ID_60000);
        toExtension.setExten(EXTENSION_60000);
        toExtension.setAppdata(APPDATA_60000);
        //when
        Mockito.when(asteriskExtensionRepo.findAsteriskExtensionsByExten(toExtensionString, Pageable.unpaged())).thenAnswer(answer -> new PageImpl<>(Collections.singletonList(toExtension)));
        Mockito.when(asteriskExtensionRepo.save(fromExtension)).thenReturn(fromExtension);
        Mockito.when(asteriskExtensionRepo.save(toExtension)).thenReturn(toExtension);

        //then
        AsteriskExtension extension = (AsteriskExtension) asteriskExtensionServiceImpl.changeExtension(fromExtension, toExtensionString);

        //assert
        assertEquals(extension.getAppdata(), toExtension.getAppdata());
    }

    @Test
    void changeExtension_MoreThanOneFromExtensionExist_ThrowsTwoMoreExtensionsFound() {
        //given
        List<AsteriskExtension> extensionList = Stream.generate(() -> new AsteriskExtension()).limit(3).collect(Collectors.toList());
        //when
        Mockito.when(asteriskExtensionRepo.findAsteriskExtensionsByExten(fromExtensionString, Pageable.unpaged())).thenAnswer(answer -> new PageImpl<>(extensionList));

        //assert
        Assertions.assertThrows(TwoOrMoreExtensionsOrPhonesFound.class, () -> asteriskExtensionServiceImpl.changeExtension(fromExtensionString, toExtensionString));
    }

    @Test
    void changeExtension_FromExtensionDoesNotExist_ThrowsZeroExtensionFound() {
        //when
        Mockito.when(asteriskExtensionRepo.findAsteriskExtensionsByExten(fromExtensionString, Pageable.unpaged())).thenAnswer(answer -> new PageImpl<>(Collections.EMPTY_LIST));

        //assert
        Assertions.assertThrows(ZeroExtensionsOrPhonesFound.class, () -> asteriskExtensionServiceImpl.changeExtension(fromExtensionString, toExtensionString));
    }


    @Test
    void findAll_ExtensionsExist_ReturnsNoEmptyList() {
        //given
        List<AsteriskExtension> initialExtensionList = new ArrayList<>(Collections.singletonList(toExtension));
        Page<AsteriskExtension> page = new PageImpl<>(initialExtensionList);
        //when
        Mockito.when(asteriskExtensionRepo.findAll(Pageable.unpaged())).thenReturn(page);


        //assert
        assertEquals(1, asteriskExtensionServiceImpl.findAll(Pageable.unpaged()).size());
    }

    @Test
    void findByExten_ExtensionExists_RetunsExtension() {
        //given
        toExtension.setExten(EXTENSION_60000);

        //when
        Mockito.when(asteriskExtensionRepo.findAsteriskExtensionsByExten(toExtensionString, Pageable.unpaged())).thenReturn(new PageImpl<>(Collections.singletonList(toExtension)));

        //assert
        assertTrue(!asteriskExtensionServiceImpl.findByExten(toExtensionString, Pageable.unpaged()).isEmpty());
        assertEquals(asteriskExtensionServiceImpl.findByExten(toExtensionString, Pageable.unpaged()).get(0).getExten(), toExtension.getExten());

    }

    @Test
    void getById_ExtensionExists_ReturnsExtensions() {
        //given
        toExtension.setId(ID_60000);
        extensionId = ID_60000;

        //when
        Mockito.when(asteriskExtensionRepo.findAllById(Collections.singletonList(extensionId))).thenReturn(Collections.singletonList(toExtension));


        //assert
        assertTrue(!asteriskExtensionServiceImpl.getById(extensionId, AsteriskExtensionType.PHONE).isEmpty());
        assertEquals(asteriskExtensionServiceImpl.getById(extensionId, AsteriskExtensionType.PHONE).get(0).getId(), toExtension.getId());

    }
}