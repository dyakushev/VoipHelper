package ru.bia.voip.vc.repo;


import ru.bia.voip.vc.model.jabber.*;
import ru.bia.voip.vc.model.jabber.rest.JabberResponse;

import java.util.Collection;
import java.util.Optional;

public interface JabberRepo {
    Optional<String> getNextFreeDirNumber();

    boolean updateUser(String userName);

    Optional<JabberDevice> createJabber(String userName, JabberType jabberType);

    Boolean checkIfUsernameExists(String userName);

    //Boolean checkIfJabberExists(String deviceName);

    Optional<JabberUser> createUser(String userName);

    Optional<JabberLine> createLine(String dirNumber);

    Boolean associateUserAndJabber(String userName, String deviceName);

    Boolean associateLineAndJabber(String dirNumber, String deviceName);

    Optional<String> getCurrentDirNumberByDevices(Collection<String> deviceList);

    Optional<Collection<String>> getDevicesByUsername(String userName);

    //Optional<JabberLine> getCurrentDirNumberByDevice(String devceName);

    Optional<UserType> getUserTypeByUsername(String userName);

    Optional<JabberResponse> getJabberByDeviceName(String deviceName);


}
