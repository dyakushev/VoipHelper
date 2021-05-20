package ru.bia.voip.vc.repo;

import ru.bia.voip.vc.model.codec.CodecResponse;

import java.util.Optional;

public interface CodecRepo {
    Optional<CodecResponse> getByDeviceName(String deviceName);

}
