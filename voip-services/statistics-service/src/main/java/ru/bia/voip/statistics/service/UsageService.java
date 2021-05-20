package ru.bia.voip.statistics.service;

import java.sql.Timestamp;
import java.util.List;

public interface UsageService {
    List<String> listUnusedDevicesThisThreeMonths();

    List<String> listUnusedDevicesThisHalfYear();

    List<String> listUnusedDevicesThisMonth();

    List<String> listUnusedDevicesBetweenDates(Timestamp from, Timestamp to);
}
