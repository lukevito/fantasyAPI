package com.gmail.com.fantasy_api;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

@Component
@Order()
public class Receiver {
    CountDownLatch latch = new CountDownLatch(1);


//    private CircularDependencyLazyB beanA;

    private void receiveMessage(String message) {
        //System.out.println("Message received:" + message);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
