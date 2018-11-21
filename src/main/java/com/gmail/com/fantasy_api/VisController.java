package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisController {

    private final VisUtil visUtil;

    @Autowired
    public VisController(VisUtil visUtil) {
        this.visUtil = visUtil;
    }

    @GetMapping(value = "/graph/{name}/options", produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getNeo4jConf(@PathVariable String name) {
        return new ResponseEntity<>(visUtil.getOptions(name), HttpStatus.OK);
    }
}
