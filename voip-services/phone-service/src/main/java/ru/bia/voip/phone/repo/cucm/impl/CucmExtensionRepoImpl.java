package ru.bia.voip.phone.repo.cucm.impl;

import org.springframework.stereotype.Repository;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import ru.bia.voip.cucm.wsdl.v12_5.*;
import ru.bia.voip.phone.conifg.cucm.AxlConfig;
import ru.bia.voip.phone.conifg.cucm.CucmExtensionConfig;
import ru.bia.voip.phone.exception.cucm.CucmLineException;
import ru.bia.voip.phone.repo.cucm.CucmExtensionRepo;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

@Repository
public class CucmExtensionRepoImpl implements CucmExtensionRepo {

    private AxlConfig axlConfig;
    private CucmExtensionConfig cucmExtensionConfig;
    private WebServiceTemplate webServiceTemplate;

    public CucmExtensionRepoImpl(WebServiceTemplate webServiceTemplate, AxlConfig axlConfig, CucmExtensionConfig cucmExtensionConfig) {
        this.webServiceTemplate = webServiceTemplate;
        this.axlConfig = axlConfig;
        this.cucmExtensionConfig = cucmExtensionConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean lineExists(String dirNumber) {
        GetLineReq getLineReq = new GetLineReq();
        getLineReq.setPattern(dirNumber);
        RLine rLine = new RLine();
        getLineReq.setReturnedTags(rLine);
        JAXBElement<GetLineReq> getLineReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "getLine"),
                        GetLineReq.class,
                        getLineReq);
        try {
            webServiceTemplate.marshalSendAndReceive(getLineReqJAXBElement);
        } catch (SoapFaultClientException e) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void createLine(String dirNumber) {
        AddLineReq addLineReq = new AddLineReq();
        XLine xLine = new XLine();
        xLine.setPattern(dirNumber);
        xLine.setRoutePartitionName(getElementFromString("routePartitionName", cucmExtensionConfig.getPartition()));
        addLineReq.setLine(xLine);
        JAXBElement<AddLineReq> addLineReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "addLine"),
                        AddLineReq.class,
                        addLineReq);
        try {
            webServiceTemplate.marshalSendAndReceive(addLineReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new CucmLineException("error while creating line with dir number " + dirNumber, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDevicesByDirNumber(String dirNumber) {
        GetLineReq getLineReq = new GetLineReq();
        getLineReq.setPattern(dirNumber);
        RLine rLine = new RLine();
        RLine.AssociatedDevices associatedDevices = new RLine.AssociatedDevices();
        rLine.setAssociatedDevices(associatedDevices);
        getLineReq.setReturnedTags(rLine);
        JAXBElement<GetLineReq> getLineReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "getLine"),
                        GetLineReq.class,
                        getLineReq);
        JAXBElement<GetLineRes> getLineResJAXBElement;
        try {
            getLineResJAXBElement = (JAXBElement<GetLineRes>) webServiceTemplate.marshalSendAndReceive(getLineReqJAXBElement);
        } catch (SoapFaultClientException | ClassCastException e) {
            throw new CucmLineException("error while getting devices associated with " + dirNumber, e);
        }
        GetLineRes getLineRes = getLineResJAXBElement.getValue();
        return getLineRes.getReturn().getLine().getAssociatedDevices().getDevice();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDeviceWithDirNumber(String deviceName, String dirNumber) {
        UpdatePhoneReq updatePhoneReq = new UpdatePhoneReq();
        updatePhoneReq.setName(deviceName);
        XPhoneLine xPhoneLine = new XPhoneLine();
        XDirn xDirn = new XDirn();
        xDirn.setPattern(dirNumber);
        xPhoneLine.setDirn(xDirn);
        UpdatePhoneReq.AddLines addLines = new UpdatePhoneReq.AddLines();
        addLines.getLine().set(0, xPhoneLine);
        updatePhoneReq.setAddLines(addLines);
        JAXBElement<UpdatePhoneReq> updatePhoneReqJAXBElement =
                new JAXBElement<>(new QName(axlConfig.getNameSpace(), "updatePhone"),
                        UpdatePhoneReq.class,
                        updatePhoneReq);
        try {
            webServiceTemplate.marshalSendAndReceive(updatePhoneReqJAXBElement);
        } catch (SoapFaultClientException e) {
            throw new CucmLineException("error while updating device " + deviceName + " with directory number " + deviceName, e);
        }
    }

    /*
     * Converts String into JAXBElement
     * */
    private JAXBElement<XFkType> getElementFromString(String namespace, String value) {
        XFkType type = new XFkType();
        type.setValue(value);
        return new JAXBElement<>(new QName(namespace), XFkType.class, type);
    }
}
