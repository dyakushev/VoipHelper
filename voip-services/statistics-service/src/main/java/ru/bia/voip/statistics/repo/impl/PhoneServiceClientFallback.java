package ru.bia.voip.statistics.repo.impl;

import org.springframework.stereotype.Component;
import ru.bia.voip.statistics.model.asterisk.AsteriskExtension;
import ru.bia.voip.statistics.model.cucm.JabberDevice;
import ru.bia.voip.statistics.repo.PhoneServiceClient;

import java.util.Collections;
import java.util.List;

@Component
public class PhoneServiceClientFallback implements PhoneServiceClient {

    @Override
    public List<AsteriskExtension> getExtenById(String id) {
        return Collections.emptyList();
    }

    @Override
    public List<JabberDevice> listJabber() {
        return Collections.emptyList();
    }
}
