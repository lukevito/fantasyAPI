package com.gmail.com.app.bean.circular;

import org.springframework.beans.factory.annotation.Autowired;

public class CircularDependencyLazyA {
    private CircularDependencyLazyB circB;

    @Autowired
    public CircularDependencyLazyA(CircularDependencyLazyB circB) {
        this.circB = circB;
    }
}
