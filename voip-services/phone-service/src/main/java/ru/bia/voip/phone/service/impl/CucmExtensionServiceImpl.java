package ru.bia.voip.phone.service.impl;

import org.springframework.stereotype.Service;
import ru.bia.voip.phone.exception.TwoOrMoreExtensionsOrPhonesFound;
import ru.bia.voip.phone.exception.ZeroExtensionsOrPhonesFound;
import ru.bia.voip.phone.model.AbstractExtension;
import ru.bia.voip.phone.model.cucm.CucmExtension;
import ru.bia.voip.phone.repo.cucm.CucmExtensionRepo;
import ru.bia.voip.phone.service.CucmExtensionService;

import java.util.List;

@Service
public class CucmExtensionServiceImpl implements CucmExtensionService<CucmExtension> {
    private CucmExtensionRepo cucmExtensionRepo;

    public CucmExtensionServiceImpl(CucmExtensionRepo cucmExtensionRepo) {
        this.cucmExtensionRepo = cucmExtensionRepo;
    }

    /*
     * 3 cases:
     * 1. Both devices exist, exchange extensions between devices.
     * 2. First device exist.
     * 3. None of devices exist.
     * */
    @Override
    public AbstractExtension changeExtension(String fromExtension, String toExtension) {
        String fromDevice, toDevice;
        List<String> fromExtensionList, toExtensionList;
        Boolean fromExtensionExists = cucmExtensionRepo.lineExists(fromExtension);
        Boolean toExtensionExists = cucmExtensionRepo.lineExists(toExtension);

        if (Boolean.TRUE.equals(fromExtensionExists)) {
            fromExtensionList = cucmExtensionRepo.getDevicesByDirNumber(fromExtension);
            if (fromExtensionList.isEmpty())
                throw new ZeroExtensionsOrPhonesFound("No devices is associated with the extension " + fromExtension);
            if (fromExtensionList.size() > 1)
                throw new TwoOrMoreExtensionsOrPhonesFound("More than one device associated with the extension " + fromExtension);
            fromDevice = fromExtensionList.get(0);

        } else throw new ZeroExtensionsOrPhonesFound("extension " + fromExtension + " does not exist");

        if (Boolean.TRUE.equals(toExtensionExists)) {
            toExtensionList = cucmExtensionRepo.getDevicesByDirNumber(toExtension);
            if (toExtensionList.isEmpty())
                cucmExtensionRepo.updateDeviceWithDirNumber(fromDevice, toExtension);
            if (fromExtensionList.size() > 1)
                throw new TwoOrMoreExtensionsOrPhonesFound("More than one device associated with the extension " + toExtension);
            toDevice = toExtensionList.get(0);
            cucmExtensionRepo.updateDeviceWithDirNumber(fromDevice, toExtension);
            cucmExtensionRepo.updateDeviceWithDirNumber(toDevice, fromExtension);
        } else {
            cucmExtensionRepo.createLine(toExtension);
            cucmExtensionRepo.updateDeviceWithDirNumber(fromDevice, toExtension);
        }

        return CucmExtension.builder().exten(toExtension).build();
    }


    @Override
    public AbstractExtension changeExtension(AbstractExtension abstractExtension, String toExtension) {
        return null;
    }

    @Override
    public List<CucmExtension> findByExten(String exten) {
        return null;
    }

}
