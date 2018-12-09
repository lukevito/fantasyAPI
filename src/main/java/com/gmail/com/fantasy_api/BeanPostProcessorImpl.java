package com.gmail.com.fantasy_api;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorImpl implements BeanPostProcessor {

    private final RabbitTemplate rabbitTemplate;
//    private final Receiver receiver;

    private int i = 0;
    int currentDepth = 0;
    String sameInstanceBean;

    public BeanPostProcessorImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
//        this.receiver = receiver;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        i++;

        String object = "[INIT_" + i + "]: Hello from [" + beanName + "], class ["+bean.getClass()+"], currentDepth " + currentDepth;
        sendRabbit(beanName, object);
        currentDepth++;
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        boolean printFactoryMsg = false;
        //TODO: to tak naprawde nie dziala dla wszyskichch przypadkow

        currentDepth--;
        if (currentDepth < 0 ) {
            System.out.println("Inna instancja ponizszego beana [ "+beanName+" ] zostala juz zainiciowana poprzez FactoryBean" );
            currentDepth = 0;
        }
        String object = "[AFTER]: Bye Bye Baby from [" + beanName + "], class ["+bean.getClass()+"], currentDepth " + currentDepth;
        sendRabbit(beanName, object);
        if (printFactoryMsg) {
            System.out.println("koniec inicializacji obiektu w FactoryBean");
        }
        return null;
    }

    private void sendRabbit(String beanName, String message) {
        System.out.println("Sending message...[" + message + "]");
        rabbitTemplate.convertAndSend(FantasyApiApplication.topicExchangeName, "foo.bar.init", message);
//        try {
//            receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
