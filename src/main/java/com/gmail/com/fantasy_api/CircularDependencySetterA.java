package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencySetterA {
    private CircularDependencySetterB circB;

    @Autowired
    public void setCircB(CircularDependencySetterB circB) {
        this.circB = circB;
    }

    public CircularDependencySetterB getCircB() {
        return circB;
    }
}
