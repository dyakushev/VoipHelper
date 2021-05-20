package ru.bia.voip.phone.repo.cucm;

import ru.bia.voip.phone.exception.jabber.JabberExtensionException;
import ru.bia.voip.phone.exception.jabber.JabberUpdateException;
import ru.bia.voip.phone.model.cucm.JabberDevice;

import java.util.List;
import java.util.Optional;

/**
 * Represents template methods to create
 * Cisco Jabber
 */
public interface JabberRepo {
    /**
     * Checks if end user exist
     *
     * @param userName represents end user userid string
     * @return true if user exists and false if does not
     */
    Boolean usernameExists(String userName);

    /**
     * Updates user parameters, for example Home cluster
     *
     * @param userName represents end user userid string
     * @throws JabberUpdateException in case of an axl call error
     */
    void updateUser(String userName);

    /**
     * Gets next unused available directory number from the provided range
     *
     * @return Optional which represents directory number
     * @throws JabberExtensionException in in case of an axl call error
     */
    Optional<String> getNextFreeDirNumber();

    /**
     * Creates new directory number based on string input
     *
     * @param dirNumber string representation of directory number
     */
    void createLine(String dirNumber);

    /**
     * Generated Jabber Desktop device name from the provided user name
     *
     * @param userName string representation of user name
     * @return Optional which represents device name
     */

    Optional<String> getDeviceNameFromUserName(String userName);

    void createJabber(String deviceName, String userName);

    void associateEndUserAndJabber(String userName, String deviceName);

    void associateAppUserAndJabber(String deviceName);

    void associateLineAndJabber(String dirNumber, String deviceName);


    JabberDevice getJabberDevice(String deviceName);

    List<String> listJabberName();

    List<JabberDevice> listJabberSql();


}
