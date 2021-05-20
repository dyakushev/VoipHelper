package ru.bia.voip.statistics.repo.cucm;

import java.time.LocalDateTime;

public interface CucmCdrRepoOperations {
    Long countCucmCdrByFromAndTimeStampBetween(String from, LocalDateTime dateFrom, LocalDateTime dateTo);
    Long countCucmCdrByToAndTimeStampBetween(String to, LocalDateTime dateFrom, LocalDateTime dateTo);

}
