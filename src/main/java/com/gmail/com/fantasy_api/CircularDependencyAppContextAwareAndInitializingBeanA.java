package com.gmail.com.fantasy_api;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//https://stackoverflow.com/questions/45210671/whats-is-the-order-dependency-injection-in-spring?noredirect=1&lq=1
//TODO: https://www.baeldung.com/spring-depends-on
//https://www.baeldung.com/circular-dependencies-in-spring
@Component
public class CircularDependencyAppContextAwareAndInitializingBeanA implements ApplicationContextAware, InitializingBean {


    private CircularDependencyAppContextAwareAndInitializingBeanB circB;

    private ApplicationContext context;

    public CircularDependencyAppContextAwareAndInitializingBeanB getCircB() {
        return circB;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        circB = context.getBean(CircularDependencyAppContextAwareAndInitializingBeanB.class);
    }

    @Override
    public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
        context = ctx;
    }
}
