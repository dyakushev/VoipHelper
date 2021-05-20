package ru.bia.voip.phone.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bia.voip.phone.model.asterisk.AsteriskExtension;
import ru.bia.voip.phone.model.asterisk.AsteriskExtensionType;
import ru.bia.voip.phone.model.cucm.CucmExtension;
import ru.bia.voip.phone.service.AsteriskExtensionService;
import ru.bia.voip.phone.service.CucmExtensionService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//GET
/*
 *
 *
 * */

//@RepositoryRestController
@RestController
@RequestMapping("/api/v1/")
public class ExtensionApiController {
    private static final Pageable PAGEABLE_DEFAULT = PageRequest.of(0, 25);

    private AsteriskExtensionService<AsteriskExtension> asteriskExtensionService;
    private CucmExtensionService<CucmExtension> cucmExtensionService;


    public ExtensionApiController(AsteriskExtensionService<AsteriskExtension> asteriskExtensionService,
                                  CucmExtensionService<CucmExtension> cucmExtensionService) {
        this.asteriskExtensionService = asteriskExtensionService;
        this.cucmExtensionService = cucmExtensionService;

    }

    @PatchMapping(path = "/extensions/{fromExten}/changeto/{toExten}", produces = "application/hal+json")
    public ResponseEntity changeExtension(@PathVariable("fromExten") String fromExten, @PathVariable("toExten") String toExten) {


        List<AsteriskExtension> extensionList = asteriskExtensionService.findByExten(fromExten, PAGEABLE_DEFAULT);
        AsteriskExtension extension;
        if (extensionList.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        extension = extensionList.get(0);
        if (extension == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        AsteriskExtension changedExtension = (AsteriskExtension) asteriskExtensionService.changeExtension(extension, toExten);

        return new ResponseEntity<>(null, HttpStatus.OK);

    }

    /**
     * @return 200 ok if extensions exist, otherwise 404 not found
     */

    @GetMapping(path = "/asterisk/extensions")
    public ResponseEntity<List<AsteriskExtension>> listAsteriskExtension() {
        List<AsteriskExtension> asteriskExtensions = asteriskExtensionService.findAll();
        if (asteriskExtensions.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(asteriskExtensions);
    }

    @GetMapping(path = "/asterisk/extensions/id/{id}")
    public ResponseEntity<List<AsteriskExtension>> listExtensionById(@PathVariable("id") @NotBlank @Size(min = 5, max = 10) String sId) {
        Integer id;
        try {
            id = Integer.parseInt(sId);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        List<AsteriskExtension> asteriskExtensions = asteriskExtensionService.getById(id, AsteriskExtensionType.PHONE);
        if (asteriskExtensions.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(asteriskExtensions);
    }
}
