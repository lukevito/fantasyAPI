package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


public class Registrar implements ImportBeanDefinitionRegistrar {

    //kilka beanow nie wejdzie do postprocesora jako, ze on sam wymaga rabbit template, a ten pociaga kolejne beany(nie wiele ~~10)
    private static final String BEAN_NAME = "postProcessor";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(BEAN_NAME)) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(BeanPostProcessorImpl.class);
            beanDefinition.setSynthetic(true);
            registry.registerBeanDefinition(BEAN_NAME, beanDefinition);
        }
    }
}
