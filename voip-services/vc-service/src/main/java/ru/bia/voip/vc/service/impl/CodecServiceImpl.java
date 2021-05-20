package ru.bia.voip.vc.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.bia.voip.vc.config.CodecConfig;
import ru.bia.voip.vc.model.codec.CodecResponse;
import ru.bia.voip.vc.repo.CodecRepo;
import ru.bia.voip.vc.repo.DeviceRepo;
import ru.bia.voip.vc.service.CodecService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Log4j2
public class CodecServiceImpl implements CodecService {
    private DeviceRepo deviceRepo;
    private CodecRepo codecRepo;
    private CodecConfig codecConfig;

    public CodecServiceImpl(DeviceRepo deviceRepo, CodecRepo codecRepo, CodecConfig codecConfig) {
        this.deviceRepo = deviceRepo;
        this.codecRepo = codecRepo;
        this.codecConfig = codecConfig;
    }

    @Override
    public Collection<CodecResponse> list() {
        Collection<CodecResponse> codecResponseCollection = new ArrayList<>();
        String devicePoolFilter = codecConfig.getDevicePool();
        Optional<Collection<String>> deviceListOptional = deviceRepo.listByDevicePoolName(devicePoolFilter);

        //check if not null and add to list
        if (deviceListOptional.isEmpty())
            return codecResponseCollection;
        deviceListOptional.get().stream().forEach(
                d -> {
                    Optional<CodecResponse> codecResponse = codecRepo.getByDeviceName(d);
                    if (codecResponse.isPresent())
                        codecResponseCollection.add(codecResponse.get());
                });
        return codecResponseCollection;
    }

}

