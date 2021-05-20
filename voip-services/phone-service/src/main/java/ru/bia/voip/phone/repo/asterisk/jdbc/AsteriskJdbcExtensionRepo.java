package ru.bia.voip.phone.repo.asterisk.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;

import java.util.List;
import java.util.Optional;

//for education purposes
public interface AsteriskJdbcExtensionRepo {

    Optional<AsteriskExtension> findById(Integer id);

    Iterable<AsteriskExtension> findAllById(List<Integer> ids);

    Page<AsteriskExtension> findAsteriskExtensionsByExten(String exten, Pageable pageable);

    int save(AsteriskExtension extension);

    Page<AsteriskExtension> findAll(Pageable pageable);

    int updateExtensionIdByExten(Integer id, String exten);
}
