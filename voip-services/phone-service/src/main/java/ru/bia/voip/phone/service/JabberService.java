package ru.bia.voip.phone.service;

import ru.bia.voip.phone.model.cucm.JabberDevice;

import java.util.List;

public interface JabberService {
    /**
     * Creates new Cisco Jabber
     * Finds unused directory number and creates line
     * Associates jabber with end user
     * Associates jabber with line
     * Associates jabber with application user
     *
     * @param userName represents string user name
     * @return created extension string
     */
    String addJabber(String userName);

    String getJabber(String userName);

    /**
     * @return list of CFS or CSF devices
     */
    List<JabberDevice> listJabber();
}
