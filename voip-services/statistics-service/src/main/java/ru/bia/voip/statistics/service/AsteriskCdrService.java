package ru.bia.voip.statistics.service;

import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AsteriskCdrService {
    List<AsteriskCdr> listByCallingNumberAndPeriod(String callingNumber, Timestamp from, Timestamp to);

    /**
     * Returns Map where String is a called directory number, Long is a count
     *
     * @param from is a start datetime
     * @param to   is an end datetime
     * @return
     */
    Map<String, Long> mapCalledExtensionToNumberOfCallsByPeriod(Timestamp from, Timestamp to);

    /**
     * Returns Map where String is a calling directory number, Long is a count
     *
     * @param from is a start datetime
     * @param to   is an end datetime
     * @return
     */
    Map<String, Long> mapCallingExtensionToNumberOfCallsByPeriod(Timestamp from, Timestamp to);
}
