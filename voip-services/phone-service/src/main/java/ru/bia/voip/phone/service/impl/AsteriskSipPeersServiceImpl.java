package ru.bia.voip.phone.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.bia.voip.phone.model.asterisk.SipPeer;
import ru.bia.voip.phone.repo.asterisk.SipPeerRepo;
import ru.bia.voip.phone.service.SipPeersService;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*@Service*/
public class AsteriskSipPeersServiceImpl implements SipPeersService<SipPeer> {
    @Autowired
    private SipPeerRepo sipPeerRepo;


    private String pattern = "\\d+\\d";

    @Override
    public List<SipPeer> findAll(Pageable pageable) {

        return sipPeerRepo.findAll(pageable).getContent();
    }


    @Override
    public List<SipPeer> findAllFilterByCallGroup(Pageable pageable, String callGroup) {
        return sipPeerRepo.findSipPeersByCallGroup(callGroup, pageable);
    }

    @Override
    public List<SipPeer> findAllFilterByIpAddress(Pageable pageable, String ipAddress) {
        return sipPeerRepo.findSipPeersByIpAddress(ipAddress, pageable);
    }

    @Override
    public List<SipPeer> findAllFilterByPickupGroup(Pageable pageable, String pickupGroup) {
        return sipPeerRepo.findSipPeersByPickupGroup(pickupGroup, pageable);
    }

    @Override
    public Optional<SipPeer> findByAsteriskExtensionAppData(String appdata) {
        String name = parseAppDataReturnSipPeerName(this.pattern, appdata);
        return sipPeerRepo.findSipPeerByName(name);
    }

    private String parseAppDataReturnSipPeerName(String patternStr, String appdata) {
        Pattern localPattern = Pattern.compile(patternStr);
        Matcher matcher = localPattern.matcher(appdata);
        if (matcher.find())
            return matcher.group();
        return "";
    }
}
