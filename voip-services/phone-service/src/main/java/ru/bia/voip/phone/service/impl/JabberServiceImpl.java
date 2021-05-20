package ru.bia.voip.phone.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.bia.voip.phone.exception.UserNameDoesNotExistException;
import ru.bia.voip.phone.exception.jabber.JabberDoesNotExist;
import ru.bia.voip.phone.model.cucm.JabberDevice;
import ru.bia.voip.phone.repo.cucm.JabberRepo;
import ru.bia.voip.phone.service.JabberService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class JabberServiceImpl implements JabberService {
    private JabberRepo jabberRepo;

    public JabberServiceImpl(JabberRepo jabberRepo) {
        this.jabberRepo = jabberRepo;
    }

    @Override
    public String getJabber(String userName) {
        Boolean usernameExists = jabberRepo.usernameExists(userName);
        if (!Boolean.TRUE.equals(usernameExists))
            throw new UserNameDoesNotExistException(userName + " does not exist. Please contact administrator");
        Optional<String> deviceNameOptional = jabberRepo.getDeviceNameFromUserName(userName);
        return jabberRepo.getJabberDevice(deviceNameOptional.orElseThrow(() -> new JabberDoesNotExist("Error while getting device name from user name"))).getDeviceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addJabber(String userName) {
        String dirNumber;
        String deviceName;
        //check if username exist
        Boolean usernameExists = jabberRepo.usernameExists(userName);
        if (!Boolean.TRUE.equals(usernameExists))
            throw new UserNameDoesNotExistException(userName + " does not exist. Please contact administrator");
        log.debug("username " + userName + " exist");
        //update users properties
        jabberRepo.updateUser(userName);
        //get next available free dir number

        dirNumber = jabberRepo.getNextFreeDirNumber().orElseThrow();
        log.debug("Available directory number is " + dirNumber);
        //create line based on dir number
        jabberRepo.createLine(dirNumber);

        deviceName = jabberRepo.getDeviceNameFromUserName(userName).orElseThrow();
        jabberRepo.createJabber(deviceName, userName);

        log.debug("Device created " + deviceName);

        jabberRepo.associateLineAndJabber(dirNumber, deviceName);

        jabberRepo.associateEndUserAndJabber(userName, deviceName);

        jabberRepo.associateAppUserAndJabber(deviceName);

        return dirNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<JabberDevice> listJabber() {
        return jabberRepo.listJabberName()
                .stream()
                .map(jn -> jabberRepo.getJabberDevice(jn))
                .collect(Collectors.toList());
    }
}
