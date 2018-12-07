package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class CircularDependencyLazyA {
    private CircularDependencyLazyB circB;

    @Autowired
    public CircularDependencyLazyA(CircularDependencyLazyB circB) {
        this.circB = circB;
    }
}
