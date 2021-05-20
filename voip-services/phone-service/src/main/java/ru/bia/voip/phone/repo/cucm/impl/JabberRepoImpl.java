package ru.bia.voip.phone.repo.cucm.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.bia.voip.cucm.wsdl.v12_5.*;
import ru.bia.voip.phone.conifg.cucm.AxlConfig;
import ru.bia.voip.phone.conifg.cucm.JabberConfig;
import ru.bia.voip.phone.exception.UserNameDoesNotExistException;
import ru.bia.voip.phone.exception.jabber.*;
import ru.bia.voip.phone.model.cucm.JabberDevice;
import ru.bia.voip.phone.repo.cucm.JabberRepo;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
@Log4j2
public class JabberRepoImpl implements JabberRepo {

    private AxlConfig axlConfig;
    private JabberConfig jabberConfig;
    private WebServiceTemplate webServiceTemplate;


    public JabberRepoImpl(JabberConfig jabberConfig, AxlConfig axlConfig, WebServiceTemplate webServiceTemplate) {

        this.jabberConfig = jabberConfig;
        this.axlConfig = axlConfig;
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public List<JabberDevice> listJabberSql() {
        ExecuteSQLQueryRes executeSQLQueryRes;

        String sql = "";
        ExecuteSQLQueryReq executeSQLQueryReq = new ExecuteSQLQueryReq();
        executeSQLQueryReq.setSql(sql);
        JAXBElement<ExecuteSQLQueryReq> executeSQLQueryReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "executeSQLQuery"),
                        ExecuteSQLQueryReq.class,
                        executeSQLQueryReq
                );
        try {
            //   JAXBElement<ExecuteSQLQueryRes> executeSQLQueryRes = (JAXBElement<ExecuteSQLQueryRes>) webServiceTemplate.marshalSendAndReceive(executeSQLQueryReqJAXBElement);
        } catch (SoapFaultClientException e) {
        }

        return null;
    }

    @Override
    public List<String> listJabberName() {
        ListPhoneRes listPhoneRes;
        ListPhoneReq listPhoneReq = new ListPhoneReq();
        LPhone lPhone = new LPhone();
        lPhone.setName("");

        listPhoneReq.setReturnedTags(lPhone);
        ListPhoneReq.SearchCriteria searchCriteria = new ListPhoneReq.SearchCriteria();
        searchCriteria.setSecurityProfileName(jabberConfig.getSecurityProfileNameDesktop());
        listPhoneReq.setSearchCriteria(searchCriteria);
        JAXBElement<ListPhoneReq> listPhoneReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "listPhone"),
                        ListPhoneReq.class,
                        listPhoneReq);
        try {
            JAXBElement<ListPhoneRes> listPhoneResJAXBElement = (JAXBElement<ListPhoneRes>) webServiceTemplate.marshalSendAndReceive(listPhoneReqJAXBElement);
            listPhoneRes = listPhoneResJAXBElement.getValue();
        } catch (SoapFaultClientException e) {
            throw new JabberDoesNotExist("Exception while getting list of jabber", e);
        }

        return listPhoneRes.getReturn()
                .getPhone()
                .stream()
                .map(rp -> rp.getName())
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(String userName) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setUserid(userName);
        updateUserReq.setHomeCluster("true");
        JAXBElement<UpdateUserReq> updateUserReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "updateUser"),
                        UpdateUserReq.class,
                        updateUserReq);
        try {
            webServiceTemplate.marshalSendAndReceive(updateUserReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new JabberUpdateException("Error while updating user " + userName, e);
        }
    }

    public JabberDevice getJabberDevice(String deviceName) {
        GetPhoneRes getPhoneRes;
        GetPhoneReq getPhoneReq = new GetPhoneReq();
        getPhoneReq.setName(deviceName);
        RPhone rPhone = new RPhone();
        rPhone.setDigestUser("");
        RPhone.Lines lines = new RPhone.Lines();

        RDirn rDirn = new RDirn();
        rDirn.setPattern("");

        RPhoneLine rPhoneLine = new RPhoneLine();
        rPhoneLine.setDirn(rDirn);
        lines.getLine().add(rPhoneLine);
        rPhone.setLines(lines);

        getPhoneReq.setReturnedTags(rPhone);
        JAXBElement<GetPhoneReq> getPhoneReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "getPhone"),
                        GetPhoneReq.class,
                        getPhoneReq);
        try {
            JAXBElement<GetPhoneRes> getPhoneResJAXBElement = (JAXBElement<GetPhoneRes>) webServiceTemplate.marshalSendAndReceive(getPhoneReqJAXBElement);
            getPhoneRes = getPhoneResJAXBElement.getValue();
        } catch (SoapFaultClientException e) {
            throw new JabberDoesNotExist("Jabber with the name " + deviceName + " does not exist", e);
        }
        String userName = getPhoneRes.getReturn()
                .getPhone()
                .getDigestUser();
        String extension = getPhoneRes.getReturn()
                .getPhone()
                .getLines()
                .getLine()
                .stream()
                .findFirst()
                .orElse(rPhoneLine)
                .getDirn()
                .getPattern();

        return JabberDevice.builder()
                .deviceName(deviceName)
                .userName(userName)
                .extension(extension)
                .build();
    }

    /*
     * Adds association between line and jabber
     * */

    @Override
    public void associateLineAndJabber(String dirNumber, String deviceName) {
        UpdatePhoneReq updatePhoneReq = new UpdatePhoneReq();
        updatePhoneReq.setName(deviceName);
        XDirn xDirn = new XDirn();
        xDirn.setPattern(dirNumber);
        xDirn.setRoutePartitionName(getElementFromString("routePartitionName", jabberConfig.getPartition()));
        XPhoneLine xPhoneLine = new XPhoneLine();
        xPhoneLine.setDirn(xDirn);
        xPhoneLine.setIndex("1");
        xPhoneLine.setBusyTrigger(jabberConfig.getBusyTrigger());
        xPhoneLine.setMaxNumCalls(jabberConfig.getMaxNumCalls());
        xPhoneLine.setRecordingProfileName(getElementFromString("recordingProfileName", jabberConfig.getRecordingProfileName()));
        xPhoneLine.setRecordingFlag(jabberConfig.getRecordingFlag());
        UpdatePhoneReq.AddLines addLines = new UpdatePhoneReq.AddLines();
        addLines.getLine().add(xPhoneLine);
        updatePhoneReq.setAddLines(addLines);
        JAXBElement<UpdatePhoneReq> updatePhoneReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "updatePhone"),
                        UpdatePhoneReq.class,
                        updatePhoneReq);
        try {
            webServiceTemplate.marshalSendAndReceive(updatePhoneReqJAXBElement);

        } catch (SoapFaultClientException e) {
            throw new JabberAssociateException("Error while associating " + dirNumber + " and " + deviceName, e);
        }
    }

    @Override
    public void associateAppUserAndJabber(String deviceName) {
        String userName = jabberConfig.getPgUser();
        RAppUser.AssociatedDevices currentAssociatedDevices;

        UpdateAppUserReq updateAppUserReq = new UpdateAppUserReq();
        updateAppUserReq.setUserid(userName);
        try {
            currentAssociatedDevices = getAppUserAssociatedDevices(userName);
        } catch (SoapFaultClientException e) {
            throw new JabberAssociateException("Error while getting devices already associated with the user " + userName, e);
        }
        UpdateAppUserReq.AssociatedDevices associatedDevices = new UpdateAppUserReq.AssociatedDevices();
        associatedDevices.getDevice().addAll(currentAssociatedDevices.getDevice());
        associatedDevices.getDevice().add(deviceName);
        updateAppUserReq.setAssociatedDevices(associatedDevices);
        JAXBElement<UpdateAppUserReq> updateAppUserReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "updateAppUser"),
                        UpdateAppUserReq.class,
                        updateAppUserReq);
        try {
            webServiceTemplate.marshalSendAndReceive(updateAppUserReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new JabberAssociateException("Error while associating " + userName + " with " + deviceName, e);
        }
    }

    /*
     * Adds association between user and jabber
     * */

    @Override
    public void associateEndUserAndJabber(String userName, String deviceName) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        RUser.AssociatedDevices currentAssociatedDevices;
        //get current associated devices
        try {
            currentAssociatedDevices = getEndUserAssociatedDevices(userName);
        } catch (ClassCastException | SoapFaultClientException e) {
            throw new JabberAssociateException("Error while getting devices already associated with the user " + userName, e);
        }
        //set user name
        updateUserReq.setUserid(userName);
        //set device name
        UpdateUserReq.AssociatedDevices associatedDevices = new UpdateUserReq.AssociatedDevices();

        associatedDevices.getDevice().addAll(currentAssociatedDevices.getDevice());
        associatedDevices.getDevice().add(deviceName);
        updateUserReq.setAssociatedDevices(associatedDevices);
        JAXBElement<UpdateUserReq> updateUserReqJAXBElement = new JAXBElement<>(
                new QName(axlConfig.getNameSpace(), "updateUser"),
                UpdateUserReq.class,
                updateUserReq
        );
        try {
            webServiceTemplate.marshalSendAndReceive(updateUserReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new JabberAssociateException("Error while associating " + userName + " with " + deviceName, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createLine(String dirNumber) {
        AddLineReq addLineReq = new AddLineReq();
        XLine xLine = new XLine();

        xLine.setRoutePartitionName(getElementFromString("routePartitionName", jabberConfig.getPartition()));
        xLine.setPattern(dirNumber);
        xLine.setShareLineAppearanceCssName(getElementFromString("shareLineAppearanceCssName", jabberConfig.getCss()));
        addLineReq.setLine(xLine);
        JAXBElement<AddLineReq> addLineReqJAXBElement = new JAXBElement<>(
                new QName(axlConfig.getNameSpace(), "addLine"),
                AddLineReq.class,
                addLineReq
        );
        try {
            webServiceTemplate.marshalSendAndReceive(addLineReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new JabberCreateException("Error while creating line for " + dirNumber, e);
        }
    }

    @Override
    public Boolean usernameExists(String userName) {
        RUser rUser = new RUser();
        rUser.setAssociatedDevices(new RUser.AssociatedDevices());
        GetUserReq getUserReq = new GetUserReq();
        getUserReq.setUserid(userName);
        getUserReq.setReturnedTags(rUser);
        JAXBElement<GetUserReq> getUserReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "getUser"),
                        GetUserReq.class,
                        getUserReq);
        try {
            webServiceTemplate.marshalSendAndReceive(getUserReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new UserNameDoesNotExistException("Username " + userName + " does not exist", e);
        }
        return true;
    }

    /*
     * Create new Jabber
     * */
    @Override
    public void createJabber(String deviceName, String userName) {
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

        //disable extension mobility, mandatory
        xphone.setEnableExtensionMobility("false");
        xphone.setBuiltInBridgeStatus("On");


        //add phone css
        xphone.setCallingSearchSpaceName(getElementFromString("callingSearchSpaceName", jabberConfig.getCss()));

        xphone.setProduct(jabberConfig.getProductDesktop());

        //vendor config
        XVendorConfig xVendorConfig = new XVendorConfig();
        try {
            xVendorConfig.getAny().add(createVendorElement("videoCapability", "0"));
        } catch (ParserConfigurationException e) {
            throw new JabberCreateException("Error while creating jabber device with the name " + deviceName, e);
        }

        xphone.setVendorConfig(xVendorConfig);
        xphone.setSecurityProfileName(getElementFromString("securityProfileName", jabberConfig.getSecurityProfileNameDesktop()));

        xphone.setName(deviceName);

        addPhoneReq.setPhone(xphone);
        JAXBElement<AddPhoneReq> addPhoneReqJAXBElement = new JAXBElement<>
                (new QName(axlConfig.getNameSpace(), "addPhone"),
                        AddPhoneReq.class,
                        addPhoneReq);

        try {
            webServiceTemplate.marshalSendAndReceive(addPhoneReqJAXBElement);

        } catch (SoapFaultClientException e) {
            throw new JabberCreateException("Error while creating jabber device with the name " + deviceName, e);
        }
    }


    @Override
    public Optional<String> getDeviceNameFromUserName(String userName) {
        String deviceName;
        deviceName = jabberConfig.getDevicePrefixDesktop() + userName.toUpperCase();
        deviceName = checkDeviceName(deviceName);
        return Optional.of(deviceName);
    }

    /*
     * Converts String into JAXBElement
     * */
    private JAXBElement<XFkType> getElementFromString(String namespace, String value) {
        XFkType type = new XFkType();
        type.setValue(value);
        return new JAXBElement<>(new QName(namespace), XFkType.class, type);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public Optional<String> getNextFreeDirNumber() {
        List<Integer> dirNumbersIntegerList;
        List<Integer> directoryRange;
        Integer next;
        Integer cucmNumberEndInteger;
        Integer cucmNumberStartInteger;
        Integer cucmNumberRange;
        List<LRoutePlan> lRoutePlanList;

        //search all dir numbers based on configured pattern
        try {
            lRoutePlanList = getLRoutePlanList();
        } catch (ClassCastException | SoapFaultClientException e) {
            throw new JabberExtensionException("Error while searching directory number by pattern " + jabberConfig.getCucmNumberSearchPattern(), e);
        }

        try {
            cucmNumberEndInteger = Integer.parseInt(jabberConfig.getCucmNumberEnd());
            cucmNumberStartInteger = Integer.parseInt(jabberConfig.getCucmNumberStart());
            if (cucmNumberEndInteger < cucmNumberStartInteger)
                throw new JabberExtensionException("CUCM end number " + cucmNumberEndInteger + "should be bigger than start number" + cucmNumberStartInteger);
            cucmNumberRange = cucmNumberEndInteger - cucmNumberStartInteger;
        } catch (NumberFormatException e) {
            throw new JabberExtensionException("Error parsing number", e);
        }
        //get currently used numbers
        dirNumbersIntegerList = getDirIntList(lRoutePlanList);
        //generate all the numbers in range
        directoryRange = IntStream.iterate(cucmNumberStartInteger, i -> i + 1).limit(cucmNumberRange).boxed().collect(Collectors.toList());

        //numbers range is empty
        if (dirNumbersIntegerList.isEmpty())
            throw new JabberExtensionException("There is no numbers on CUCM in the range between " + cucmNumberStartInteger + " and " + cucmNumberEndInteger);
        //remove existing numbers from range to find which are free
        directoryRange.removeAll(dirNumbersIntegerList);
        if (directoryRange.isEmpty())
            throw new JabberExtensionException("There is no numbers available in the range between " + cucmNumberStartInteger + " and " + cucmNumberEndInteger);
        //get first available number
        next = directoryRange.get(0);
        //if element is out of range
        if (next > cucmNumberEndInteger)
            throw new JabberExtensionException("There is no numbers available in the range between " + cucmNumberStartInteger + " and " + cucmNumberEndInteger);

        String nextDirNumber = String.valueOf(next);
        return Optional.of(nextDirNumber);
    }
    /*
     * Gets LRoutePlan list from AXL API
     *
     * */

    private List<LRoutePlan> getLRoutePlanList() {
        List<LRoutePlan> lRoutePlanList = null;
        ListRoutePlanRes listRoutePlanRes = null;
        ListRoutePlanReq listRoutePlanReq = new ListRoutePlanReq();
        ListRoutePlanReq.SearchCriteria searchCriteria = new ListRoutePlanReq.SearchCriteria();

        searchCriteria.setDnOrPattern(jabberConfig.getCucmNumberSearchPattern());
        listRoutePlanReq.setSearchCriteria(searchCriteria);
        LRoutePlan lRoutePlan = new LRoutePlan();
        lRoutePlan.setDnOrPattern("");
        listRoutePlanReq.setReturnedTags(lRoutePlan);
        JAXBElement<ListRoutePlanReq> listRoutePlanReqJAXBElement = new JAXBElement<>
                (new QName(axlConfig.getNameSpace(), "listRoutePlan"),
                        ListRoutePlanReq.class,
                        listRoutePlanReq);
        JAXBElement<ListRoutePlanRes> listRoutePlanResJAXBElement = (JAXBElement<ListRoutePlanRes>) webServiceTemplate.marshalSendAndReceive(listRoutePlanReqJAXBElement);
        listRoutePlanRes = listRoutePlanResJAXBElement.getValue();
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

        if (lRoutePlanList != null && !lRoutePlanList.isEmpty()) {
            lRoutePlanList.forEach(lRoutePlan -> dirNumbersStringList.add(lRoutePlan.getDnOrPattern()));
        }
        if (!dirNumbersStringList.isEmpty())
            dirNumbersStringList.forEach(a -> dirNumbersIntegerList.add(Integer.parseInt(a)));
        if (!dirNumbersIntegerList.isEmpty()) {
            Collections.sort(dirNumbersIntegerList);
        }
        return dirNumbersIntegerList;
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

    /*
     * Get current associated devices
     * */
    private RUser.AssociatedDevices getEndUserAssociatedDevices(String userName) {
        GetUserRes getUserRes = null;
        GetUserReq getUserReq = new GetUserReq();
        getUserReq.setUserid(userName);
        RUser.AssociatedDevices associatedDevices = new RUser.AssociatedDevices();
        RUser rUser = new RUser();
        rUser.setAssociatedDevices(associatedDevices);
        getUserReq.setReturnedTags(rUser);
        JAXBElement<GetUserReq> getUserReqJAXBElement = new JAXBElement<>(
                new QName(axlConfig.getNameSpace(), "getUser"),
                GetUserReq.class,
                getUserReq);
        JAXBElement<GetUserRes> getUserResJAXBElement = (JAXBElement<GetUserRes>) webServiceTemplate.marshalSendAndReceive(getUserReqJAXBElement);
        getUserRes = getUserResJAXBElement.getValue();
        return getUserRes.getReturn().getUser().getAssociatedDevices();
    }

    private RAppUser.AssociatedDevices getAppUserAssociatedDevices(String userName) {
        GetAppUserRes getAppUserRes;
        GetAppUserReq getAppUserReq = new GetAppUserReq();
        getAppUserReq.setUserid(userName);
        RAppUser rAppUser = new RAppUser();
        rAppUser.setAssociatedDevices(new RAppUser.AssociatedDevices());
        getAppUserReq.setReturnedTags(rAppUser);
        JAXBElement<GetAppUserReq> getAppUserReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "getAppUser"),
                        GetAppUserReq.class,
                        getAppUserReq);
        JAXBElement<GetAppUserRes> getAppUserResJAXBElement = (JAXBElement<GetAppUserRes>) webServiceTemplate.marshalSendAndReceive(getAppUserReqJAXBElement);
        getAppUserRes = getAppUserResJAXBElement.getValue();
        return getAppUserRes.getReturn().getAppUser().getAssociatedDevices();
    }

    private Element createVendorElement(String name, String value) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();

        // create the root element node
        Element element = doc.createElement(name);
        element.setTextContent(value);
        return element;
    }

}
