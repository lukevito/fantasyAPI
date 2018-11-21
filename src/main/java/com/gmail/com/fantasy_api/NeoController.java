package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NeoController {

    private final NeoUtil neoUtil;

    @Autowired
    public NeoController(NeoUtil neoUtil) {
        this.neoUtil = neoUtil;
    }

    @GetMapping(value = "/neo4jConf", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getNeo4jConf() {
        return new ResponseEntity<>(neoUtil.getNeo4jConf(), HttpStatus.OK);
    }


}
