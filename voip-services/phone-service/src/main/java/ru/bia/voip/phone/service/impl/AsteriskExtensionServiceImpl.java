package ru.bia.voip.phone.service.impl;

import com.google.common.collect.MoreCollectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.bia.voip.phone.exception.TwoOrMoreExtensionsOrPhonesFound;
import ru.bia.voip.phone.exception.ZeroExtensionsOrPhonesFound;
import ru.bia.voip.phone.model.AbstractExtension;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;
import ru.bia.voip.phone.model.asterisk.AsteriskExtensionType;
import ru.bia.voip.phone.repo.asterisk.AsteriskExtensionRepo;
import ru.bia.voip.phone.service.AsteriskExtensionService;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AsteriskExtensionServiceImpl implements AsteriskExtensionService<AsteriskExtension> {
    private final AsteriskExtensionRepo asteriskExtensionRepo;

    public AsteriskExtensionServiceImpl(@Qualifier("asteriskExtensionRepo") AsteriskExtensionRepo asteriskExtensionRepo) {
        this.asteriskExtensionRepo = asteriskExtensionRepo;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AbstractExtension changeExtension(String fromExtensionString, String toExtensionString) {
        AbstractExtension fromExtension = getExtensionFromList(fromExtensionString);
        return this.changeExtension(fromExtension, toExtensionString);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public AbstractExtension changeExtension(AbstractExtension fromExtension, String toExtensionString) {
        AbstractExtension toExtension;
        try {
            toExtension = getExtensionFromList(toExtensionString);
            extensionExchange((AsteriskExtension) fromExtension, (AsteriskExtension) toExtension);
        } catch (ZeroExtensionsOrPhonesFound e) {
            extensionExchange((AsteriskExtension) fromExtension, toExtensionString);
        }
        return getExtensionFromList(toExtensionString);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public List<AsteriskExtension> findAll(Pageable pageable) {
        return asteriskExtensionRepo.findAll(pageable).getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AsteriskExtension> findAll() {
        return this.findAll(Pageable.unpaged());
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public List<AsteriskExtension> findByExten(String exten, Pageable pageable) {
        return asteriskExtensionRepo.findAsteriskExtensionsByExten(exten, pageable).getContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AsteriskExtension> findByExten(String exten) {
        return this.findByExten(exten, Pageable.unpaged());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<AsteriskExtension> getById(Integer id, AsteriskExtensionType asteriskExtensionType) {
        return StreamSupport.stream(asteriskExtensionRepo.findAllByIdAndApp(id, asteriskExtensionType.toString()).spliterator(), false).collect(Collectors.toList());
    }


    private void extensionExchange(AsteriskExtension fromExtension, AsteriskExtension toExtension) {
        String fromAppdata = fromExtension.getAppdata();
        String toAppdata = toExtension.getAppdata();
        fromExtension.setAppdata(toAppdata);
        toExtension.setAppdata(fromAppdata);
        asteriskExtensionRepo.save(fromExtension);
        asteriskExtensionRepo.save(toExtension);
    }


    private void extensionExchange(AsteriskExtension fromExtension, String toExtension) {
        fromExtension.setExten(toExtension);
        asteriskExtensionRepo.save(fromExtension);
        asteriskExtensionRepo.updateExtensionIdByExten(Integer.parseInt(toExtension), toExtension);

    }

    private AbstractExtension getExtensionFromList(String extenionString) {
        AbstractExtension extension;
        List<AsteriskExtension> extensionList = this.findByExten(extenionString);
        try {
            extension = extensionList.stream().collect(MoreCollectors.onlyElement());
        } catch (IllegalArgumentException e) {
            throw new TwoOrMoreExtensionsOrPhonesFound("More than one extension found for " + extenionString + ". Please contact administrator", e);
        } catch (NoSuchElementException e) {
            throw new ZeroExtensionsOrPhonesFound("Zero extension found for " + extenionString + ". Please contact administrator", e);
        }
        return extension;
    }
}
