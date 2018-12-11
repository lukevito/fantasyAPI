package com.gmail.com.app.bean.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencyAppContextAwareAndInitializingBeanB {

    private CircularDependencyAppContextAwareAndInitializingBeanA circA;

    private String message = "Hi!";

    @Autowired
    public void setCircA(CircularDependencyAppContextAwareAndInitializingBeanA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
