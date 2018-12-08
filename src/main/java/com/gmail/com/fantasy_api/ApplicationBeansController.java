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
    public ApplicationBeansController.ApplicationBeans beans() {
        Map<String, ApplicationBeansController.ContextBeans> contexts = new HashMap<>();
        ConfigurableApplicationContext context = this.context;
        while (context != null) {
            contexts.put(context.getId(), ApplicationBeansController.ContextBeans.describing(context));
            context = getConfigurableParent(context);
        }
        return new ApplicationBeansController.ApplicationBeans(contexts);
    }

    private static ConfigurableApplicationContext getConfigurableParent(
            ConfigurableApplicationContext context) {
        ApplicationContext parent = context.getParent();
        if (parent instanceof ConfigurableApplicationContext) {
            return (ConfigurableApplicationContext) parent;
        }
        return null;
    }

    /**
     * A description of an application's beans, primarily intended for serialization to
     * JSON.
     */
    public static final class ApplicationBeans {

        private final Map<String, ApplicationBeansController.ContextBeans> contexts;

        private ApplicationBeans(Map<String, ApplicationBeansController.ContextBeans> contexts) {
            this.contexts = contexts;
        }

        public Map<String, ApplicationBeansController.ContextBeans> getContexts() {
            return this.contexts;
        }

    }

    /**
     * A description of an application context, primarily intended for serialization to
     * JSON.
     */
    public static final class ContextBeans {

        private final Map<String, ApplicationBeansController.BeanDescriptor> beans;

        private final String parentId;

        private ContextBeans(Map<String, ApplicationBeansController.BeanDescriptor> beans, String parentId) {
            this.beans = beans;
            this.parentId = parentId;
        }

        public String getParentId() {
            return this.parentId;
        }

        public Map<String, ApplicationBeansController.BeanDescriptor> getBeans() {
            return this.beans;
        }

        private static ApplicationBeansController.ContextBeans describing(ConfigurableApplicationContext context) {
            if (context == null) {
                return null;
            }
            ConfigurableApplicationContext parent = getConfigurableParent(context);
            return new ApplicationBeansController.ContextBeans(describeBeans(context.getBeanFactory()),
                    (parent != null) ? parent.getId() : null);
        }

        private static Map<String, ApplicationBeansController.BeanDescriptor> describeBeans(
                ConfigurableListableBeanFactory beanFactory) {
            Map<String, ApplicationBeansController.BeanDescriptor> beans = new HashMap<>();
            for (String beanName : beanFactory.getBeanDefinitionNames()) {
                BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
                if (isBeanEligible(beanName, definition, beanFactory)) {
                    beans.put(beanName, describeBean(beanName, definition, beanFactory));
                }
            }
            return beans;
        }

        private static ApplicationBeansController.BeanDescriptor describeBean(String name, BeanDefinition definition,
                                                                 ConfigurableListableBeanFactory factory) {
            return new ApplicationBeansController.BeanDescriptor(factory.getAliases(name), definition.getScope(),
                    factory.getType(name), definition.getResourceDescription(),
                    factory.getDependenciesForBean(name));
        }

        private static boolean isBeanEligible(String beanName, BeanDefinition bd,
                                              ConfigurableBeanFactory bf) {
            return (bd.getRole() != BeanDefinition.ROLE_INFRASTRUCTURE
                    && (!bd.isLazyInit() || bf.containsSingleton(beanName)));
        }

    }

    /**
     * A description of a bean in an application context, primarily intended for
     * serialization to JSON.
     */
    public static final class BeanDescriptor {

        private final String[] aliases;

        private final String scope;

        private final Class<?> type;

        private final String resource;

        private final String[] dependencies;

        private BeanDescriptor(String[] aliases, String scope, Class<?> type,
                               String resource, String[] dependencies) {
            this.aliases = aliases;
            this.scope = (StringUtils.hasText(scope) ? scope
                    : BeanDefinition.SCOPE_SINGLETON);
            this.type = type;
            this.resource = resource;
            this.dependencies = dependencies;
        }

        public String[] getAliases() {
            return this.aliases;
        }

        public String getScope() {
            return this.scope;
        }

        public Class<?> getType() {
            return this.type;
        }

        public String getResource() {
            return this.resource;
        }

        public String[] getDependencies() {
            return this.dependencies;
        }

    }


}
