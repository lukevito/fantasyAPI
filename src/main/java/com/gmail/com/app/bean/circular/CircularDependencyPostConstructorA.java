package com.gmail.com.app.bean.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularDependencyPostConstructorA {

    @Autowired
    private CircularDependencyPostConstructorB circB;

    @PostConstruct
    public void init() {
        circB.setCircA(this);
    }

    public CircularDependencyPostConstructorB getCircB() {
        return circB;
    }
}
