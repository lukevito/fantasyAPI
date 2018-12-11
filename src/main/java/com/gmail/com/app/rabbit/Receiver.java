package com.gmail.com.app.rabbit;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.concurrent.CountDownLatch;

@Component
@Order()
public class Receiver {
    CountDownLatch latch = new CountDownLatch(1);

    private void receiveMessage(String message) {
        System.out.println("Message received:" + message);
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
