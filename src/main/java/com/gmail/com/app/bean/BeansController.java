package com.gmail.com.app.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BeansController {

    private final ConfigurableApplicationContext context;

    @Autowired
    public BeansController(ConfigurableApplicationContext context) {
        this.context = context;
    }

    /**
     *                       .cypher/graph-cypher-skratch.cyp
     */
    @GetMapping(value = "/all-beans", produces= MediaType.APPLICATION_JSON_VALUE)
    public BeansUtill.ApplicationBeans beans() {
        return getApplicationBeans(this.context);
    }

    public static BeansUtill.ApplicationBeans getApplicationBeans(ConfigurableApplicationContext context2) {
        Map<String, BeansUtill.ContextBeans> contexts = new HashMap<>();
        ConfigurableApplicationContext context = context2;
        while (context != null) {
            contexts.put(context.getId(), BeansUtill.ContextBeans.describing(context));
            context = BeansUtill.getConfigurableParent(context);
        }
        return new BeansUtill.ApplicationBeans(contexts);
    }


}
