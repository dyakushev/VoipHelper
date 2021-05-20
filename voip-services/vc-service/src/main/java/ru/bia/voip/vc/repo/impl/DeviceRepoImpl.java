package ru.bia.voip.vc.repo.impl;

import org.springframework.stereotype.Repository;
import ru.bia.voip.cucm.wsdl.v10_5.*;
import ru.bia.voip.vc.repo.DeviceRepo;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DeviceRepoImpl implements DeviceRepo {
    private AXLPort axlPort;


    public DeviceRepoImpl(AXLPort axlPort, AXLPort axlPort2) {
        this.axlPort = axlPort;

    }

    @Override
    public Optional<Collection<String>> listByDevicePoolName(String devicePoolName) {
        Collection<String> deviceList = new ArrayList<>();
        ListPhoneRes listPhoneRes = null;
        ListPhoneReq listPhoneReq = new ListPhoneReq();
        LPhone lPhone = new LPhone();
        lPhone.setName("");
        listPhoneReq.setReturnedTags(lPhone);
        ListPhoneReq.SearchCriteria searchCriteria = new ListPhoneReq.SearchCriteria();
        searchCriteria.setDevicePoolName(devicePoolName);
        listPhoneReq.setSearchCriteria(searchCriteria);
        try {
            listPhoneRes = axlPort.listPhone(listPhoneReq);
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }
        List<LPhone> lPhoneList = listPhoneRes.getReturn().getPhone();
        if (lPhoneList == null)
            return Optional.empty();
        if (lPhoneList.isEmpty())
            return Optional.empty();
        lPhoneList.forEach(l -> deviceList.add(l.getName()));
        return Optional.of(deviceList);
    }

}
