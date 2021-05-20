package ru.bia.voip.statistics.repo;

import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AsteriskCdrRepo {
    /**
     * @param from
     * @param to
     * @param callingNumber
     * @return
     */

    List<AsteriskCdr> listCdrsByDateAndCallingNumber(Timestamp from, Timestamp to, String callingNumber);


    // List<AsteriskCdr> listCdrsByDateAndCalledNumber(Timestamp from, Timestamp to, String callingNumber);

    /**
     * Returns rows count between dates and by clid
     *
     * @param from          datetime
     * @param to            datetime
     * @param callingNumber represents asterisk clid
     * @return Optional long, which represents number of found calls
     */


    Optional<Long> getCountByDateAndCallingNumber(Timestamp from, Timestamp to, String callingNumber);

    /**
     * @param from
     * @param to
     * @param number
     * @return
     */
    Optional<Long> getCountByDateAndCallingNumberAndCalledNumber(Timestamp from, Timestamp to, String number);

    List<String> listCallingNumbersByDate(Timestamp from, Timestamp to);

    List<String> listCalledNumbersByDate(Timestamp from, Timestamp to);

    Map<String, Long> mapExtensionToNumberOfIncomingCallsByDate(Timestamp from, Timestamp to, List<String> dstList);

    Map<String, Long> mapExtensionToNumberOfOutgoingCallsByDate(Timestamp from, Timestamp to, List<String> srcList);
}
