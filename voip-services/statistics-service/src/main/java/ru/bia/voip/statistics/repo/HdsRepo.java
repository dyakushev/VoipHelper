package ru.bia.voip.statistics.repo;

import java.sql.Timestamp;
import java.util.List;

public interface HdsRepo {

    /**
     * @param from beginning date
     * @param to   ending date
     * @return list of extensions who was logged in
     */
    List<String> listDistinctLoggedExtensionsBetweenDates(Timestamp from, Timestamp to);

}
