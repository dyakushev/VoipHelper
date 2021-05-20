package ru.bia.voip.phone.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bia.voip.phone.exception.jabber.JabberDoesNotExist;
import ru.bia.voip.phone.model.cucm.JabberDevice;
import ru.bia.voip.phone.model.cucm.rest.JabberRequest;
import ru.bia.voip.phone.model.cucm.rest.JabberResponse;
import ru.bia.voip.phone.service.JabberService;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/jabber_cc")
public class JabberApiController {

    private JabberService jabberService;

    public JabberApiController(JabberService jabberService) {
        this.jabberService = jabberService;
    }

    @GetMapping("{userName}")
    public ResponseEntity<String> getJabber(@PathVariable @NotNull @Max(50) String userName) {
        try {
            String deviceName = jabberService.getJabber(userName);
            return ResponseEntity.ok(deviceName);
        } catch (JabberDoesNotExist e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }

    }

    @PostMapping
    public ResponseEntity<JabberResponse> addJabber(@RequestBody JabberRequest jabberRequest) {
        String extension = jabberService.addJabber(jabberRequest.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(JabberResponse.builder().extension(extension).build());
    }

    @GetMapping
    public ResponseEntity<List<JabberDevice>> listJabber() {
        return ResponseEntity.ok(jabberService.listJabber());
    }
}
