package ru.bia.voip.statistics.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.bia.voip.statistics.model.cucm.JabberDevice;
import ru.bia.voip.statistics.repo.HdsRepo;
import ru.bia.voip.statistics.repo.PhoneServiceClient;
import ru.bia.voip.statistics.repo.cucm.CucmCdrRepo;
import ru.bia.voip.statistics.service.UsageService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * How to determine that jabber is not in use
 * 1. Check if an agent used jabber's extension during login. We can check it using HDS Agent Logout table.
 * 2. Check if there were calls with the jabber's extension
 */


@Service
@Log4j2
public class JabberCCUsageServiceImpl implements UsageService {
    private HdsRepo hdsRepo;
    private PhoneServiceClient phoneServiceClient;
    private CucmCdrRepo cucmCdrRepo;

    public JabberCCUsageServiceImpl(HdsRepo hdsRepo, PhoneServiceClient phoneServiceClient, CucmCdrRepo cucmCdrRepo) {
        this.phoneServiceClient = phoneServiceClient;
        this.hdsRepo = hdsRepo;
        this.cucmCdrRepo = cucmCdrRepo;
    }

    @Override
    public List<String> listUnusedDevicesThisMonth() {
        LocalDateTime thisMonth = LocalDateTime.now();
        Timestamp from = Timestamp.valueOf(thisMonth
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
        );
        Timestamp to = Timestamp.valueOf(thisMonth);

        return this.listUnusedDevicesBetweenDates(from, to);
    }

    @Override
    public List<String> listUnusedDevicesThisThreeMonths() {
        LocalDateTime previousMonth = LocalDateTime.now()
                .minusMonths(3);
        Timestamp from = Timestamp.valueOf(previousMonth
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
        );
        Timestamp to = Timestamp.valueOf(previousMonth.
                withDayOfMonth(previousMonth.getMonth().maxLength())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)

        );
        return this.listUnusedDevicesBetweenDates(from, to);
    }

    @Override
    public List<String> listUnusedDevicesThisHalfYear() {
        LocalDateTime previousMonth = LocalDateTime.now()
                .minusMonths(6);
        Timestamp from = Timestamp.valueOf(previousMonth
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
        );
        Timestamp to = Timestamp.valueOf(previousMonth.
                withDayOfMonth(previousMonth.getMonth().maxLength())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)

        );
        return this.listUnusedDevicesBetweenDates(from, to);
    }

    @Override
    public List<String> listUnusedDevicesBetweenDates(Timestamp from, Timestamp to) {
        List<JabberDevice> jabberDevices = phoneServiceClient.listJabber();
        List<String> loggedExtensions = hdsRepo.listDistinctLoggedExtensionsBetweenDates(from, to);
        List<JabberDevice> notLoggedDevices = getNotLoggedDevices(jabberDevices, loggedExtensions);
        return getDevicesWithNoCalls(notLoggedDevices, from.toLocalDateTime(), to.toLocalDateTime())
                .stream().map(d -> d.getExtension()).collect(Collectors.toList());
    }

    /**
     * @param jabberDevices    jabber devices currently configured on Cisco UCM
     * @param loggedExtensions extensions that were used while logging onto Cisco Finesse
     * @return list of jabber devices, which extensions were never used to log in
     */

    private List<JabberDevice> getNotLoggedDevices(List<JabberDevice> jabberDevices, List<String> loggedExtensions) {
        return jabberDevices.stream().filter(jd -> !loggedExtensions.contains(jd.getExtension())).collect(Collectors.toList());
    }

    /**
     * @param jabberDevices jabber devices list
     * @param from          start of the time period
     * @param to            end of the time period
     * @return list of devices which had 0 calls for the given time period
     */

    private List<JabberDevice> getDevicesWithNoCalls(List<JabberDevice> jabberDevices, LocalDateTime from, LocalDateTime to) {
        ZonedDateTime zdtTo = to.atZone(ZoneOffset.UTC);
        ZonedDateTime zdtFrom = from.atZone(ZoneOffset.UTC);
        return jabberDevices.stream().filter(d ->
                cucmCdrRepo.countCucmCdrByFromAndTimeStampBetween(d.getExtension(), zdtFrom.toLocalDateTime(), zdtTo.toLocalDateTime()) == 0
                        || cucmCdrRepo.countCucmCdrByToAndTimeStampBetween(d.getExtension(), zdtFrom.toLocalDateTime(), zdtFrom.toLocalDateTime()) == 0)
                .collect(Collectors.toList());
    }
}
