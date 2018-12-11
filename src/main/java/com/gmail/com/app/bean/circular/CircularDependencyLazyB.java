package com.gmail.com.app.bean.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencyLazyB {

    private CircularDependencyLazyA circA;

    @Autowired
    public CircularDependencyLazyB(@Lazy CircularDependencyLazyA circA) {
        this.circA = circA;
    }
}
