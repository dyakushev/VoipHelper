package ru.bia.voip.vc.repo;

import java.util.Collection;
import java.util.Optional;

public interface DeviceRepo {
    Optional<Collection<String>> listByDevicePoolName(String devicePoolName);
}
