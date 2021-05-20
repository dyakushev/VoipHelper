package ru.bia.voip.vc.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.bia.voip.vc.config.JabberConfig;
import ru.bia.voip.vc.model.jabber.*;
import ru.bia.voip.vc.model.jabber.rest.JabberAddResponse;
import ru.bia.voip.vc.model.jabber.rest.JabberResponse;
import ru.bia.voip.vc.repo.DeviceRepo;
import ru.bia.voip.vc.repo.JabberRepo;
import ru.bia.voip.vc.service.JabberService;

import java.util.*;

@Service
@Log4j2
public class JabberServiceImpl implements JabberService {
    private JabberRepo jabberRepo;
    private DeviceRepo deviceRepo;
    private JabberConfig jabberConfig;


    public JabberServiceImpl(JabberRepo jabberRepo, DeviceRepo deviceRepo, JabberConfig jabberConfig) {
        this.deviceRepo = deviceRepo;
        this.jabberRepo = jabberRepo;
        this.jabberConfig = jabberConfig;
    }

    @Override
    public Optional<JabberAddResponse> add(String userName, Set<JabberType> jabberTypeList) {
        JabberAddResponse jabberResponse;
        JabberUser jabberUser;
        JabberLine jabberLine;
        JabberDevice jabberDevice;
        List<JabberDevice> jabberDeviceList = new ArrayList<>();
        String dirNumber;
        Optional<String> dirNumberOptional;
        Optional<JabberLine> jabberLineOptional;
        Optional<JabberDevice> jabberDeviceOptional;


        //check if username exists and create jabber user
        boolean usernameExists = jabberRepo.checkIfUsernameExists(userName);

        if (usernameExists) {
            log.debug("user {} exists", userName);
            //update user with settings for cisco jabber
            if (!jabberRepo.updateUser(userName))
                return Optional.empty();
            Optional<UserType> userTypeOptional = jabberRepo.getUserTypeByUsername(userName);
            jabberUser = JabberUser.builder().userName(userName).userType(userTypeOptional.get()).build();
        } else {
            Optional<JabberUser> jabberUserOptional = jabberRepo.createUser(userName);
            if (jabberUserOptional.isPresent()) {
                jabberUser = jabberUserOptional.get();
                log.debug("user {} has been created", jabberUser);
            } else {
                log.error("User for {} creation error", userName);
                return Optional.empty();
            }

        }


        //get directory number and create line
        Optional<Collection<String>> deviceListOptional = jabberRepo.getDevicesByUsername(userName);
        if (deviceListOptional.isEmpty()) {
            log.debug("There is no associated devices for {}", userName);
            dirNumberOptional = Optional.empty();
        } else
            dirNumberOptional = jabberRepo.getCurrentDirNumberByDevices(deviceListOptional.get());
        if (dirNumberOptional.isEmpty()) {
            dirNumberOptional = jabberRepo.getNextFreeDirNumber();
            if (dirNumberOptional.isEmpty())
                return Optional.empty();
            dirNumber = dirNumberOptional.get();
            log.debug("next available number is {}", dirNumber);
            //create line
            jabberLineOptional = jabberRepo.createLine(dirNumber);
        } else {
            dirNumber = dirNumberOptional.get();
            log.debug("Current number is {}", dirNumber);
            jabberLineOptional = Optional.of(JabberLine.builder().dirNumber(dirNumber).build());
        }

        if (jabberLineOptional.isEmpty()) {
            log.error("Line for {} creation error", dirNumberOptional.get());
            return Optional.empty();
        }
        jabberLine = jabberLineOptional.get();
        log.debug("Line {} has been created", jabberLine);


        for (JabberType jabberType : jabberTypeList
        ) {
            jabberDeviceOptional = jabberRepo.createJabber(jabberUser.getUserName(), jabberType);
            if (jabberDeviceOptional.isPresent()) {
                jabberDevice = jabberDeviceOptional.get();
                jabberRepo.associateLineAndJabber(jabberLine.getDirNumber(), jabberDevice.getName());
                jabberRepo.associateUserAndJabber(jabberUser.getUserName(), jabberDevice.getName());
                log.debug("Jabber {} has been created", jabberDevice);
                jabberDevice.setJabberType(jabberType);
                jabberDeviceList.add(jabberDevice);
            } else
                log.error("Jabber for type {} and username {} creation error", jabberType, userName);
        }
        jabberResponse = JabberAddResponse.builder().jabberDevices(jabberDeviceList).jabberLine(jabberLine).jabberUser(jabberUser).build();
        return Optional.of(jabberResponse);
    }

    @Override
    public Optional<Collection<JabberResponse>> getByUsername(String userName) {
        Collection<JabberResponse> jabberResponseList = new ArrayList<>();
        //check if username exist
        boolean usernameExists = jabberRepo.checkIfUsernameExists(userName);
        if (!usernameExists)
            return Optional.empty();
        //creating list of devices
        Optional<Collection<String>> jabberNameListOptional = jabberRepo.getDevicesByUsername(userName);
        if (jabberNameListOptional.isPresent()) {

            jabberNameListOptional.get().forEach(deviceName -> {
                        Optional<JabberResponse> jabberResponseOptional = jabberRepo.getJabberByDeviceName(deviceName);
                        if (jabberResponseOptional.isPresent())
                            jabberResponseList.add(jabberResponseOptional.get());
                    }
            );

        }

        return Optional.of(jabberResponseList);
    }

    @Override
    public Optional<Collection<JabberResponse>> getByType(JabberType jabberType) {
        Collection<JabberResponse> jabberResponseList = new ArrayList<>();
        Optional<Collection<String>> deviceNameListOptional = deviceRepo.listByDevicePoolName(jabberConfig.getDevicePoolName());
        if (deviceNameListOptional.isEmpty())
            return Optional.empty();
        Collection<String> deviceNameList = deviceNameListOptional.get();
        deviceNameList.stream().filter(deviceName -> getJabberTypeByDeviceName(deviceName).equals(jabberType)).forEach(deviceName -> {
            Optional<JabberResponse> jabberResponseOptional = jabberRepo.getJabberByDeviceName(deviceName);
            if (jabberResponseOptional.isPresent())
                jabberResponseList.add(jabberResponseOptional.get());
        });
        return Optional.of(jabberResponseList);
    }

    @Override
    public Optional<Collection<JabberResponse>> list() {
        Collection<JabberResponse> jabberResponseList = new ArrayList<>();
        Optional<Collection<String>> deviceNameListOptional = deviceRepo.listByDevicePoolName(jabberConfig.getDevicePoolName());
        if (deviceNameListOptional.isEmpty())
            return Optional.empty();
        Collection<String> deviceNameList = deviceNameListOptional.get();
        deviceNameList.forEach(deviceName -> jabberResponseList.add(jabberRepo.getJabberByDeviceName(deviceName).get()));
        return Optional.of(jabberResponseList);
    }

    /*
     * Resolves jabber type based on Name
     *
     * */
    private JabberType getJabberTypeByDeviceName(String deviceName) {
        if (deviceName.startsWith("CFS") || deviceName.startsWith("CSF"))
            return JabberType.DESKTOP;
        if (deviceName.startsWith("TCT"))
            return JabberType.IPHONE;
        if (deviceName.startsWith("TAB"))
            return JabberType.TABLET;
        if (deviceName.startsWith("BOT"))
            return JabberType.ANDROID;
        return null;
    }

}

