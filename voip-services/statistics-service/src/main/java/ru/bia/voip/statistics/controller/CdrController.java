package ru.bia.voip.statistics.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.bia.voip.statistics.model.asterisk.AsteriskCdr;
import ru.bia.voip.statistics.service.AsteriskCdrService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/api/v1/cdr")
public class CdrController {

    private AsteriskCdrService asteriskCdrService;

    @Autowired
    public void setAsteriskCdrService(AsteriskCdrService asteriskCdrService) {
        this.asteriskCdrService = asteriskCdrService;
    }

    @GetMapping(value = "/calls", produces = "application/json")
    public ResponseEntity<Collection<AsteriskCdr>> getCdrsFromTo(@RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                                                 @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                                                 @RequestParam("number") String number
    ) {
        Collection<AsteriskCdr> cdrs = asteriskCdrService.listByCallingNumberAndPeriod(number, Timestamp.valueOf(from), Timestamp.valueOf(to));
        if (cdrs.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(cdrs);
    }
}
