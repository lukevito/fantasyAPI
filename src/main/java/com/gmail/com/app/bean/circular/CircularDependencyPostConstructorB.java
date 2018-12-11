package com.gmail.com.app.bean.circular;

import org.springframework.stereotype.Component;

@Component
public class CircularDependencyPostConstructorB {

    private CircularDependencyPostConstructorA circA;

    private String message = "Hi!";

    public void setCircA(CircularDependencyPostConstructorA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
