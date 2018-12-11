package com.gmail.com.fantasy_api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApplicationBeansController {

    private final ConfigurableApplicationContext context;

    /**
     * Creates a new {@code ApplicationBeansController} that will describe the beans in the given
     * {@code context} and all of its ancestors.
     * @param context the application context
     * @see ApplicationBeansController#getParent()
     */
    @Autowired
    public ApplicationBeansController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @GetMapping(value = "/all-beans", produces= MediaType.APPLICATION_JSON_VALUE)
    public ApplicationBeansUtill.ApplicationBeans beans() {
        return getApplicationBeans(this.context);
    }

    static ApplicationBeansUtill.ApplicationBeans getApplicationBeans(ConfigurableApplicationContext context2) {
        Map<String, ApplicationBeansUtill.ContextBeans> contexts = new HashMap<>();
        ConfigurableApplicationContext context = context2;
        while (context != null) {
            contexts.put(context.getId(), ApplicationBeansUtill.ContextBeans.describing(context));
            context = ApplicationBeansUtill.getConfigurableParent(context);
        }
        return new ApplicationBeansUtill.ApplicationBeans(contexts);
    }


}
