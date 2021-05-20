package ru.bia.voip.vc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bia.voip.vc.model.codec.CodecResponse;
import ru.bia.voip.vc.service.CodecService;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/codec")
public class CodecController {
    private CodecService codecService;


    public CodecController(CodecService codecService) {
        this.codecService = codecService;

    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Collection<CodecResponse>> listCodec() {
        Collection<CodecResponse> codecResponseList = codecService.list();
        if (codecResponseList == null)
            return ResponseEntity.notFound().build();
        if (codecResponseList.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(codecResponseList);
    }
}
