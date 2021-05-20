package ru.bia.voip.statistics.repo.cucm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.bia.voip.statistics.model.cucm.CucmCdr;

import java.time.LocalDateTime;

@Repository
public interface CucmCdrRepo extends ElasticsearchRepository<CucmCdr, String> {
    Page<CucmCdr> findCucmCdrByTimeStampBetween(LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    Long countCucmCdrByFromAndTimeStampBetween(String from, LocalDateTime dateFrom, LocalDateTime dateTo);


    Long countCucmCdrByToAndTimeStampBetween(String to, LocalDateTime dateFrom, LocalDateTime dateTo);

}
