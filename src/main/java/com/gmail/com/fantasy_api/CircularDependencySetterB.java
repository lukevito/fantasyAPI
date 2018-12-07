package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencySetterB {
    private CircularDependencySetterA circA;

    private String message = "Hi!";

    @Autowired
    public void setCircA(CircularDependencySetterA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
