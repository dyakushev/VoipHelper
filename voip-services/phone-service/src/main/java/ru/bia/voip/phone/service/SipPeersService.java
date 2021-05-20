package ru.bia.voip.phone.service;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SipPeersService<T> {
    List<T> findAll(Pageable pageable);



    List<T> findAllFilterByCallGroup(Pageable pageable, String callGroup);

    List<T> findAllFilterByIpAddress(Pageable pageable, String ipAddress);

    List<T> findAllFilterByPickupGroup(Pageable pageable, String pickupGroup);

    Optional<T> findByAsteriskExtensionAppData(String appdata);

}
