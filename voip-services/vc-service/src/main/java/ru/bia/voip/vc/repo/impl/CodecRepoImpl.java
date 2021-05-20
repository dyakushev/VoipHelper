package ru.bia.voip.vc.repo.impl;

import org.springframework.stereotype.Repository;
import ru.bia.voip.cucm.wsdl.v10_5.*;
import ru.bia.voip.vc.model.codec.CodecResponse;
import ru.bia.voip.vc.repo.CodecRepo;

import java.util.Optional;

@Repository
public class CodecRepoImpl implements CodecRepo {
    private AXLPort axlPort;

    public CodecRepoImpl(AXLPort axlPort) {
        this.axlPort = axlPort;
    }

    @Override
    public Optional<CodecResponse> getByDeviceName(String deviceName) {
        GetPhoneRes getPhoneRes = null;
        GetPhoneReq getPhoneReq = new GetPhoneReq();
        getPhoneReq.setName(deviceName);
        RPhone rPhone = new RPhone();
        rPhone.setModel("");
        rPhone.setDescription("");
        RPhone.Lines lines = new RPhone.Lines();
        RPhoneLine rPhoneLine = new RPhoneLine();
        RDirn rDirn = new RDirn();
        rDirn.setPattern("");
        rPhoneLine.setDirn(rDirn);
        lines.getLine().add(rPhoneLine);
        rPhone.setLines(lines);
        getPhoneReq.setReturnedTags(rPhone);
        try {
            getPhoneRes = axlPort.getPhone(getPhoneReq);
        } catch (AXLError_Exception e) {
            return Optional.empty();
        }
        RPhone returnedRPhone = getPhoneRes.getReturn().getPhone();
        String dirNumber = returnedRPhone.getLines().getLine().get(0).getDirn().getPattern();
        CodecResponse codecResponse = CodecResponse.builder().name(deviceName).description(returnedRPhone.getDescription()).deviceType(returnedRPhone.getModel()).dirNumber(dirNumber).build();
        return Optional.of(codecResponse);
    }
}
