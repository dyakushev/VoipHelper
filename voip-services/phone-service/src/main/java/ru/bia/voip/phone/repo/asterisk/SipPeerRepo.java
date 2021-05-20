package ru.bia.voip.phone.repo.asterisk;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.bia.voip.phone.model.asterisk.SipPeer;

import java.util.List;
import java.util.Optional;

/*@Repository*//*
@Transactional("asteriskTransactionManager")*/
public interface SipPeerRepo extends PagingAndSortingRepository<SipPeer, Integer> {
    List<SipPeer> findSipPeersByCallGroup(String callGroup, Pageable pageable);

    List<SipPeer> findSipPeersByIpAddress(String ipAddress, Pageable pageable);

    List<SipPeer> findSipPeersByPickupGroup(String pickupGroup, Pageable pageable);

    Optional<SipPeer> findSipPeerByName(String name);

}
