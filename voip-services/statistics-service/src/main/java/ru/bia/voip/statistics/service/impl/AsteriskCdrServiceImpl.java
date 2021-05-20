package ru.bia.voip.statistics.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;
import ru.bia.voip.statistics.model.asterisk.AsteriskExtension;
import ru.bia.voip.statistics.repo.AsteriskCdrRepo;
import ru.bia.voip.statistics.repo.PhoneServiceClient;
import ru.bia.voip.statistics.service.AsteriskCdrService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AsteriskCdrServiceImpl implements AsteriskCdrService {

    private AsteriskCdrRepo asteriskCdrRepo;
    private PhoneServiceClient asteriskExtensionClient;

    @Autowired
    public void setAsteriskExtensionClient(PhoneServiceClient asteriskExtensionClient) {
        this.asteriskExtensionClient = asteriskExtensionClient;
    }

    @Autowired
    public void setAsteriskCdrRepo(AsteriskCdrRepo asteriskCdrRepo) {
        this.asteriskCdrRepo = asteriskCdrRepo;
    }

    @Override
    public List<AsteriskCdr> listByCallingNumberAndPeriod(String callingNumber, Timestamp dateFrom, Timestamp dateTo) {
        return asteriskCdrRepo.listCdrsByDateAndCallingNumber(dateFrom, dateTo, callingNumber);
    }

    @Override
    public Map<String, Long> mapCalledExtensionToNumberOfCallsByPeriod(Timestamp from, Timestamp to) {
        List<String> extensionList = asteriskCdrRepo.listCalledNumbersByDate(from, to);

        List<String> filteredExtensionList = extensionList.stream()
                .filter(getAsteriskExtensionPredicate())
                .collect(Collectors.toList());

        return asteriskCdrRepo.mapExtensionToNumberOfIncomingCallsByDate(from, to, filteredExtensionList);
    }

    @Override
    public Map<String, Long> mapCallingExtensionToNumberOfCallsByPeriod(Timestamp from, Timestamp to) {
        List<String> extensionList = asteriskCdrRepo.listCallingNumbersByDate(from, to);
        //first filter all asterisk extensions
        List<String> filteredExtensionList = extensionList.stream()
                .filter(getAsteriskExtensionPredicate().or(getAsteriskIdPredicate()))
                .collect(Collectors.toList());
        Map<String, Long> extensionMap = asteriskCdrRepo.mapExtensionToNumberOfOutgoingCallsByDate(from, to, filteredExtensionList);
        //replace all ids with the real exten
        Map<String, Long> filteredMap = extensionMap.keySet()
                .stream()
                .filter(getAsteriskIdPredicate())
                .collect(Collectors.toMap(id -> getExtenById(id), id -> extensionMap.get(id)));
        //add filtered values from initial map to filtered map
        filteredMap.putAll(extensionMap.keySet()
                .stream()
                .filter(getAsteriskExtensionPredicate())
                .collect(Collectors.toMap(ext -> ext, ext -> extensionMap.get(ext))));

        return filteredMap;
    }

    //return ext by id using phone-service rest api
    private String getExtenById(String id) {
        List<AsteriskExtension> extensionList = asteriskExtensionClient.getExtenById(id);
        if (extensionList.isEmpty() || extensionList.size() > 1)
            return "";
        return extensionList.get(0).getExten();
    }

    //checks the following pattern 6XXXX or 7XXXX
    private Predicate<String> getAsteriskExtensionPredicate() {
        Predicate<String> asteriskExtPattern6XXXX = ext -> ext.startsWith("6");
        Predicate<String> asteriskExtPattern7XXXX = ext -> ext.startsWith("7");
        Predicate<String> asteriskExtPatternLength = ext -> ext.length() == 5;

        Predicate<String> asteriskExt6XXXXPredicate = asteriskExtPattern6XXXX.and(asteriskExtPatternLength);
        Predicate<String> asteriskExt7XXXXPredicate = asteriskExtPattern7XXXX.and(asteriskExtPatternLength);

        // Predicate<String> asteriskExtPredicate = asteriskExt6XXXXPredicate.or(asteriskExt7XXXXPredicate);
        return asteriskExt6XXXXPredicate.or(asteriskExt7XXXXPredicate);
    }

    //checks the following pattern 10_XXX_XXX - 10_XXX_XXX_XX and 20_XXX_XXX - 20_XXX_XXX_XX
    private Predicate<String> getAsteriskIdPredicate() {
        Predicate<String> asteriskIdLen8 = ext -> ext.length() >= 8;
        Predicate<String> asteriskIdLen10 = ext -> ext.length() <= 10;
        Predicate<String> asteriskIdLen = asteriskIdLen8.and(asteriskIdLen10);

        Predicate<String> asteriskIdPattern10_ = ext -> ext.startsWith("10");
        Predicate<String> asteriskIdPattern20_ = ext -> ext.startsWith("20");
        Predicate<String> asteriskIdPattern = asteriskIdPattern10_.and(asteriskIdPattern20_);

        return asteriskIdLen.and(asteriskIdPattern);
    }
}
