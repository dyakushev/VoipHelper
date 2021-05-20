package ru.bia.voip.vc.controller;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bia.voip.vc.model.jabber.JabberType;
import ru.bia.voip.vc.model.jabber.rest.JabberAddResponse;
import ru.bia.voip.vc.model.jabber.rest.JabberRequest;
import ru.bia.voip.vc.model.jabber.rest.JabberResponse;
import ru.bia.voip.vc.service.JabberService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
@RequestMapping("/api/v1/jabber_ts")
public class JabberController {

    private JabberService jabberService;

    public JabberController(JabberService jabberService) {
        this.jabberService = jabberService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<JabberAddResponse> addJabber(@RequestBody @Valid JabberRequest jabberRequest) {
        Set<JabberType> jabberTypeSet = new HashSet<>();
        jabberRequest.getTypes().stream().forEach(a -> jabberTypeSet.add(JabberType.valueOf(a)));
        Optional<JabberAddResponse> jabberResponseOptional = jabberService.add(jabberRequest.getUserName(), jabberTypeSet);
        if (jabberResponseOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.ok(jabberResponseOptional.get());
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Collection<JabberResponse>> getJabber(@RequestParam(name = "username", required = false) String userName, @RequestParam(name = "type", required = false) JabberType jabberType) {
        Optional<Collection<JabberResponse>> jabberResponseOptional = Optional.empty();
        if (userName != null)
            jabberResponseOptional = jabberService.getByUsername(userName);
        else if (jabberType != null)
            jabberResponseOptional = jabberService.getByType(jabberType);

        if (jabberResponseOptional.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(jabberResponseOptional.get());
    }

}
