package ru.bia.voip.phone.repo.cucm;

import java.util.List;

/**
 * Represents template methods to change CUCM extension
 */
public interface CucmExtensionRepo {

    /**
     * Checks if directory number exists or does not
     *
     * @param dirNumber represents directory number
     * @return true if exits and false in the opposite case
     */
    boolean lineExists(String dirNumber);

    /**
     * Creates line based on the directory number provided
     *
     * @param dirNumber
     */

    void createLine(String dirNumber);

    List<String> getDevicesByDirNumber(String dirNumber);

    void updateDeviceWithDirNumber(String deviceName, String dirNumber);

}
