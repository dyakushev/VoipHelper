package ru.bia.voip.vc.service;

import ru.bia.voip.vc.model.codec.CodecResponse;

import java.util.Collection;

public interface CodecService {

    Collection<CodecResponse> list();

}
