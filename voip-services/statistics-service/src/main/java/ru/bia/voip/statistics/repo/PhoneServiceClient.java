package ru.bia.voip.statistics.repo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.bia.voip.statistics.model.asterisk.AsteriskExtension;
import ru.bia.voip.statistics.model.cucm.JabberDevice;
import ru.bia.voip.statistics.repo.impl.PhoneServiceClientFallback;

import java.util.List;

@FeignClient(name = "phone-service",
        fallback = PhoneServiceClientFallback.class
)
public interface PhoneServiceClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/asterisk/extensions/id/{id}", consumes = "application/json")
    List<AsteriskExtension> getExtenById(@PathVariable("id") String id);

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/jabber_cc", consumes = "application/json")
    List<JabberDevice> listJabber();
}
