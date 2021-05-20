package ru.bia.voip.vc.repo.impl;

import com.google.common.collect.Iterables;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.bia.voip.cucm.wsdl.v10_5.*;
import ru.bia.voip.vc.config.JabberConfig;
import ru.bia.voip.vc.model.jabber.*;
import ru.bia.voip.vc.model.jabber.rest.JabberResponse;
import ru.bia.voip.vc.repo.JabberRepo;
import ru.bia.voip.vc.util.RandomPasswordGenerator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.*;

@Component
@Log4j2
public class JabberRepoImpl implements JabberRepo {
    private AXLPort axlPort;
    private JabberConfig jabberConfig;
    private RandomPasswordGenerator randomPasswordGenerator;


    @Autowired
    public void setRandomPasswordGenerator(RandomPasswordGenerator randomPasswordGenerator,
                                           JabberConfig jabberRepoConfig,
                                           AXLPort axlPort) {
        this.randomPasswordGenerator = randomPasswordGenerator;
        this.jabberConfig = jabberRepoConfig;
        this.axlPort = axlPort;
    }


    @Override
    public Optional<String> getCurrentDirNumberByDevices(Collection<String> deviceList) {
        String deviceName = Iterables.get(deviceList, 0);
        GetPhoneRes getPhoneRes = null;
        GetPhoneReq getPhoneReq = new GetPhoneReq();
        RPhone rPhone = new RPhone();
        RPhone.Lines lines = new RPhone.Lines();
        RPhoneLine rPhoneLine = new RPhoneLine();
        RDirn rDirn = new RDirn();
        rDirn.setPattern("");
        rPhoneLine.setDirn(rDirn);
        lines.getLine().add(rPhoneLine);
        rPhone.setLines(lines);
        getPhoneReq.setName(deviceName);
        getPhoneReq.setReturnedTags(rPhone);
        try {
            getPhoneRes = axlPort.getPhone(getPhoneReq);
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }
        if (getPhoneRes == null)
            return Optional.empty();
        RPhone.Lines rphoneLines = getPhoneRes.getReturn().getPhone().getLines();
        if (rphoneLines == null)
            return Optional.empty();
        List<RPhoneLine> rPhoneLines = rphoneLines.getLine();
        if (rPhoneLines == null)
            return Optional.empty();
        if (rPhoneLines.size() == 0)
            return Optional.empty();
        String dirNumber = rPhoneLines.get(0).getDirn().getPattern();
        return Optional.ofNullable(dirNumber);
    }



 /*   @Override
    public Boolean checkIfJabberExists(String deviceName) {
        GetPhoneRes getPhoneRes = null;
        GetPhoneReq getPhoneReq = new GetPhoneReq();
        RPhone rPhone = new RPhone();

        rPhone.setName("");
        getPhoneReq.setReturnedTags(rPhone);
        getPhoneReq.setName(deviceName);
        try {
            getPhoneRes = axlPort.getPhone(getPhoneReq);
        } catch (AXLError_Exception e) {
            log.error(e.getFaultInfo().getAxlmessage());
            ;
            return false;
        }
        if (getPhoneRes != null)
            if (getPhoneRes.getReturn().getPhone().getName().equals(deviceName))
                return true;
        return false;

    }*/

    @Override
    public Boolean associateLineAndJabber(String dirNumber, String deviceName) {
        StandardResponse standardResponse = null;
        UpdatePhoneReq updatePhoneReq = new UpdatePhoneReq();
        updatePhoneReq.setName(deviceName);
        XDirn xDirn = new XDirn();
        xDirn.setPattern(dirNumber);
        xDirn.setRoutePartitionName(getElementFromString("routePartitionName", jabberConfig.getPartition()));
        XPhoneLine xPhoneLine = new XPhoneLine();
        xPhoneLine.setDirn(xDirn);
        xPhoneLine.setIndex("1");
        UpdatePhoneReq.AddLines addLines = new UpdatePhoneReq.AddLines();
        addLines.getLine().add(xPhoneLine);
        updatePhoneReq.setAddLines(addLines);

        try {
            standardResponse = axlPort.updatePhone(updatePhoneReq);
        } catch (AXLError_Exception e) {
            log.error(e.getFaultInfo().getAxlmessage());
            ;
            return false;
        }
        if (standardResponse != null)
            return true;
        return false;
    }

    /*
     * Create new directory number
     * */
    @Override
    public Optional<JabberLine> createLine(String dirNumber) {
        StandardResponse standardResponse = null;
        String uid = null;
        AddLineReq addLineReq = new AddLineReq();
        XLine xLine = new XLine();
        xLine.setRoutePartitionName(getElementFromString("routePartitionName", jabberConfig.getPartition()));
        xLine.setPattern(dirNumber);
        xLine.setShareLineAppearanceCssName(getElementFromString("shareLineAppearanceCssName", jabberConfig.getCss()));

        addLineReq.setLine(xLine);
        try {
            standardResponse = axlPort.addLine(addLineReq);
        } catch (AXLError_Exception e) {
            log.error(e.getFaultInfo().getAxlmessage());
            return Optional.empty();
        }
        if (standardResponse == null)
            return Optional.empty();

        uid = standardResponse.getReturn();
        JabberLine jabberLine = JabberLine.builder().dirNumber(dirNumber).uid(uid).build();
        return Optional.of(jabberLine);

    }


    /*
     * Adds association between user and jabber
     * */

    @Override
    public Boolean associateUserAndJabber(String userName, String deviceName) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        //get current associated devices
        RUser.AssociatedDevices currentDevices = getAssociatedDevices(userName);
        //set user name
        updateUserReq.setUserid(userName);
        //set device name
        UpdateUserReq.AssociatedDevices associatedDevices = new UpdateUserReq.AssociatedDevices();
        if (currentDevices != null)
            associatedDevices.getDevice().addAll(currentDevices.getDevice());
        associatedDevices.getDevice().add(deviceName);
        updateUserReq.setAssociatedDevices(associatedDevices);
        try {
            axlPort.updateUser(updateUserReq);
        } catch (AXLError_Exception e) {
            log.error(e.getFaultInfo().getAxlmessage());
            return false;
        }

        return true;
    }

    /*
     * Creates new user with the provided username
     * */
    @Override
    public Optional<JabberUser> createUser(String userName) {
        StandardResponse standardResponse = null;
        String userUid = null;

        AddUserReq addUserReq = new AddUserReq();
        XUser xUser = new XUser();
        //User ID
        xUser.setUserid(userName);
        //Password
        String password = randomPasswordGenerator.generateRandomSpecialCharacters();
        xUser.setPassword(password);
        //Last name
        xUser.setLastName(userName);
        //User groups
        XUser.AssociatedGroups associatedGroups = new XUser.AssociatedGroups();
        XUser.AssociatedGroups.UserGroup userGroup = new XUser.AssociatedGroups.UserGroup();
        for (String group : jabberConfig.getUserGroups()
        ) {
            userGroup.setName(group);
        }
        associatedGroups.getUserGroup().add(userGroup);
        xUser.setAssociatedGroups(associatedGroups);
        //Enable User for Unified CM IM and Presence
        xUser.setImAndPresenceEnable("true");
        //UC Service Profile
        xUser.setServiceProfile(getElementFromString("serviceProfile", jabberConfig.getUcServiceProfile()));

        addUserReq.setUser(xUser);
        try {
            standardResponse = axlPort.addUser(addUserReq);
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }

        userUid = standardResponse.getReturn();
        JabberUser jabberUser = JabberUser.builder().userName(userName).userType(UserType.LOCAL).password(password).uid(userUid).build();
        return Optional.of(jabberUser);
    }

    /*
     *
     * Check if user exists
     * */
    @Override
    public Boolean checkIfUsernameExists(String userName) {
        GetUserRes getUserRes = null;
        RUser rUser = new RUser();
        //  rUser.setAssociatedGroups(null);

        GetUserReq getUserReq = new GetUserReq();
        getUserReq.setUserid(userName);


        try {
            getUserRes = axlPort.getUser(getUserReq);
        } catch (Exception e) {

        }
        if (getUserRes != null) {
            rUser = getUserRes.getReturn().getUser();
        } else rUser = null;

        return rUser == null ? false : true;
    }

    /*
     * Create new Jabber
     * */
    @Override
    public Optional<JabberDevice> createJabber(String userName, JabberType jabberType) {
        String deviceName = null, deviceUid = null;
        StandardResponse standardResponse = null;
        AddPhoneReq addPhoneReq = new AddPhoneReq();
        XPhone xphone = new XPhone();
        //description
        xphone.setDescription(userName);
        //protocol
        xphone.setProtocol("SIP");
        xphone.setProtocolSide("User");
        xphone.setClazz("Phone");
        //digest user
        xphone.setDigestUser(userName);
        //owner user
        if (!jabberConfig.isUseLicenseUser())
            xphone.setOwnerUserName(getElementFromString("ownerUserName", userName));
        else
            xphone.setOwnerUserName(getElementFromString("ownerUserName", jabberConfig.getLicenseUser()));
        //device pool
        xphone.setDevicePoolName(getElementFromString("devicePoolName", jabberConfig.getDevicePoolName()));
        //Media resouce group list
        xphone.setMediaResourceListName(getElementFromString("mediaResourceListName", jabberConfig.getMrglName()));
        //disable extension mobility, mandatory
        xphone.setEnableExtensionMobility("false");

        //add phone css
        xphone.setCallingSearchSpaceName(getElementFromString("callingSearchSpaceName", jabberConfig.getCss()));

        xphone.setProduct(jabberConfig.getProduct(jabberType));
        deviceName = jabberConfig.getDevicePrefix(jabberType) + userName.toUpperCase();
        xphone.setSecurityProfileName(getElementFromString("securityProfileName", jabberConfig.getSecurityProfileName(jabberType)));

        deviceName = checkDeviceName(deviceName);
        xphone.setName(deviceName);

        addPhoneReq.setPhone(xphone);

        try {
            standardResponse = axlPort.addPhone(addPhoneReq);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        deviceUid = standardResponse.getReturn();
        JabberDevice jabberDevice = JabberDevice.builder().name(deviceName).uid(deviceUid).build();
        return Optional.of(jabberDevice);
    }


    /*
     * Gets next empty dir number
     * */
    @Override
    public Optional<String> getNextFreeDirNumber() {
        Integer next, cucmNumberEndInteger;
        List<LRoutePlan> lRoutePlanList = getLRoutePlanList();

        List<Integer> dirNumbersIntegerList = getDirIntList(lRoutePlanList);
        Integer dirNumbersIntegerListSize = dirNumbersIntegerList.size();


        try {
            cucmNumberEndInteger = Integer.parseInt(jabberConfig.getCucmNumberEnd());
        } catch (NumberFormatException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
        //numbers range is empty
        if (dirNumbersIntegerList.isEmpty())
            return Optional.empty();
        next = dirNumbersIntegerList.get(dirNumbersIntegerListSize - 1);
        //last element is out of range
        if (next >= cucmNumberEndInteger)
            return Optional.empty();
        next++;
        String nextDirNumber = String.valueOf(next);
        return Optional.of(nextDirNumber);
    }
    /*
     * Gets LRoutePlan list from AXL API
     *
     * */

    private List<LRoutePlan> getLRoutePlanList() {
        List<LRoutePlan> lRoutePlanList = null;

        ListRoutePlanRes listRoutePlanRes = new ListRoutePlanRes();
        ListRoutePlanReq listRoutePlanReq = new ListRoutePlanReq();
        ListRoutePlanReq.SearchCriteria searchCriteria = new ListRoutePlanReq.SearchCriteria();

        searchCriteria.setDnOrPattern(jabberConfig.getCucmNumberStart());
        listRoutePlanReq.setSearchCriteria(searchCriteria);
        LRoutePlan lRoutePlan = new LRoutePlan();
        lRoutePlan.setDnOrPattern("");
        listRoutePlanReq.setReturnedTags(lRoutePlan);
        try {
            listRoutePlanRes = axlPort.listRoutePlan(listRoutePlanReq);
        } catch (AXLError_Exception e) {
            log.error(e.getFaultInfo().getAxlmessage());
            ;
        }
        if (listRoutePlanRes != null)
            lRoutePlanList = listRoutePlanRes.getReturn().getRoutePlan();

        return lRoutePlanList;
    }

    /*
     * Converts lRoutePlanList into sorted Integer list
     * */
    public List<Integer> getDirIntList(List<LRoutePlan> lRoutePlanList) {
        List<String> dirNumbersStringList = new ArrayList<>();
        List<Integer> dirNumbersIntegerList = new ArrayList<>();

        if (lRoutePlanList != null)
            if (!lRoutePlanList.isEmpty()) {
                lRoutePlanList.forEach(lRoutePlan -> dirNumbersStringList.add(lRoutePlan.getDnOrPattern()));
            }
        if (!dirNumbersStringList.isEmpty())
            dirNumbersStringList.forEach(a ->
                    {
                        try {

                            dirNumbersIntegerList.add(Integer.parseInt(a));
                        } catch (NumberFormatException e) {
                            //  log.info(e);

                        }

                    }

            );
        if (!dirNumbersIntegerList.isEmpty()) {
            Collections.sort(dirNumbersIntegerList);
        }
        return dirNumbersIntegerList;
    }
    /*
     *
     * Get devices associated with the user
     *
     * */

    @Override
    public Optional<Collection<String>> getDevicesByUsername(String userName) {
        GetUserRes getUserRes = null;
        GetUserReq getUserReq = new GetUserReq();
        RUser rUser = new RUser();
        rUser.setAssociatedDevices(new RUser.AssociatedDevices());
        getUserReq.setUserid(userName);
        getUserReq.setReturnedTags(rUser);
        try {
            getUserRes = axlPort.getUser(getUserReq);//.getReturn().getUser().getAssociatedDevices();
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }

        RUser.AssociatedDevices associatedDevices = getUserRes.getReturn().getUser().getAssociatedDevices();
        if (associatedDevices == null)
            return Optional.empty();
        List<String> deviceList = associatedDevices.getDevice();
        if (deviceList == null)
            return Optional.empty();
        if (deviceList.size() == 0)
            return Optional.empty();
        return Optional.ofNullable(deviceList);
    }


    @Override
    public Optional<UserType> getUserTypeByUsername(String userName) {
        GetUserRes getUserRes = null;
        GetUserReq getUserReq = new GetUserReq();
        RUser rUser = new RUser();
        rUser.setLdapDirectoryName(getElementFromString("ldapDirectoryName", "").getValue());
        getUserReq.setUserid(userName);
        getUserReq.setReturnedTags(rUser);
        try {
            getUserRes = axlPort.getUser(getUserReq);//.getReturn().getUser().getAssociatedDevices();
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }

        String accountType = getUserRes.getReturn().getUser().getLdapDirectoryName().getValue();
        UserType userType = getUserTypeFromString(accountType);
        return Optional.of(userType);
    }

    @Override
    public boolean updateUser(String userName) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setUserid(userName);
        updateUserReq.setHomeCluster("true");
        updateUserReq.setImAndPresenceEnable("true");
        updateUserReq.setServiceProfile(getElementFromString("serviceProfile", jabberConfig.getUcServiceProfile()));

        UpdateUserReq.AssociatedGroups associatedGroups = new UpdateUserReq.AssociatedGroups();
        UpdateUserReq.AssociatedGroups.UserGroup userGroup = new UpdateUserReq.AssociatedGroups.UserGroup();
        for (String group : jabberConfig.getUserGroups()
        ) {
            userGroup.setName(group);
        }
        associatedGroups.getUserGroup().add(userGroup);
        updateUserReq.setAssociatedGroups(associatedGroups);

        try {
            axlPort.updateUser(updateUserReq);
        } catch (AXLError_Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Optional<JabberResponse> getJabberByDeviceName(String deviceName) {
        GetPhoneRes getPhoneRes;
        Collection<String> dirNumbers = new ArrayList<>();
        GetPhoneReq getPhoneReq = new GetPhoneReq();
        getPhoneReq.setName(deviceName);
        RPhone rPhoneReq = new RPhone();
        RPhone.Lines lines = new RPhone.Lines();
        RPhoneLine rPhoneLine = new RPhoneLine();
        RDirn rDirn = new RDirn();
        rDirn.setPattern("");
        rPhoneLine.setDirn(rDirn);
        lines.getLine().add(rPhoneLine);
        rPhoneReq.setLines(lines);
        rPhoneReq.setModel("");
        rPhoneReq.setDigestUser("");
        getPhoneReq.setReturnedTags(rPhoneReq);
        try {
            getPhoneRes = axlPort.getPhone(getPhoneReq);
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }
        RPhone rPhoneRes = getPhoneRes.getReturn().getPhone();
        String dirNumber = "";

        List<RPhoneLine> rPhoneLineList = rPhoneRes.getLines().getLine();
        if (rPhoneLineList != null)
            rPhoneLineList.forEach(rp -> dirNumbers.add(rp.getDirn().getPattern()));

        JabberResponse jabberGetResponse = JabberResponse.builder().deviceName(deviceName).dirNumbers(dirNumbers).jabberType(getJabberTypeFromString(rPhoneRes.getModel())).userName(rPhoneRes.getDigestUser()).build();
        return Optional.of(jabberGetResponse);
    }


    private JabberType getJabberTypeFromString(String model) {
        if (model == null)
            return null;
        if (model.isEmpty())
            return null;
        switch (model) {
            case "Cisco Dual Mode for Android":
                return JabberType.ANDROID;
            case "Cisco Unified Client Services Framework":
                return JabberType.DESKTOP;
            case "Cisco Dual Mode for iPhone":
                return JabberType.IPHONE;
            case "Cisco Jabber for Tablet":
                return JabberType.TABLET;
            default:
                break;
        }
        return null;
    }

    private UserType getUserTypeFromString(String userType) {
        if (userType == null || userType.isEmpty())
            return UserType.LOCAL;
        else
            return UserType.DOMAIN;
    }

    /*
     * Converts String into JAXBElement
     * */
    private JAXBElement<XFkType> getElementFromString(String namespace, String value) {
        XFkType type = new XFkType();
        type.setValue(value);
        JAXBElement<XFkType> element = new JAXBElement<>(new QName(namespace), XFkType.class, type);
        return element;
    }


    /*
     * Get current associated devices
     * */
    private RUser.AssociatedDevices getAssociatedDevices(String userName) {

        GetUserRes getUserRes = null;
        GetUserReq getUserReq = new GetUserReq();
        getUserReq.setUserid(userName);
        RUser.AssociatedDevices associatedDevices = new RUser.AssociatedDevices();
        RUser rUser = new RUser();
        rUser.setAssociatedDevices(associatedDevices);
        getUserReq.setReturnedTags(rUser);
        try {
            getUserRes = axlPort.getUser(getUserReq);
        } catch (AXLError_Exception e) {
            return null;
        }
        if (getUserRes != null)
            return getUserRes.getReturn().getUser().getAssociatedDevices();
        else
            return null;
    }

    /*
     * Modify device name according to regex and device name length
     * */
    private String checkDeviceName(String deviceName) {
        deviceName = deviceName.replaceAll("[^a-zA-Z0-9]", "");
        StringBuilder deviceNameBuilder = new StringBuilder(deviceName);
        StringBuilder newDeviceName = new StringBuilder();
        int deviceNameSize = deviceName.length();
        if (deviceNameSize > jabberConfig.getDeviceNameMaxLength()) {
            newDeviceName.append(deviceNameBuilder.substring(0, 3)).append(deviceNameBuilder.substring(deviceNameSize - jabberConfig.getDeviceNameMaxLength() + 3));
            deviceName = newDeviceName.toString();
        }
        return deviceName;
    }
}
