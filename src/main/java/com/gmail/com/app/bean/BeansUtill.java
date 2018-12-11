package com.gmail.com.app.bean;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BeansUtill {

    //https://www.baeldung.com/circular-dependencies-in-spring
    //https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/
    //https://stackoverflow.com/questions/1088550/how-to-call-a-method-after-bean-initialization-is-complete
    //https://blog.jdriven.com/2015/04/spicy-spring-dynamically-create-your-own-beandefinition/

    public static ConfigurableApplicationContext getConfigurableParent(
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

        private final Map<String, BeansUtill.ContextBeans> contexts;

        public ApplicationBeans(Map<String, BeansUtill.ContextBeans> contexts) {
            this.contexts = contexts;
        }

        public Map<String, BeansUtill.ContextBeans> getContexts() {
            return this.contexts;
        }

    }

    /**
     * A description of an application context, primarily intended for serialization to
     * JSON.
     */
    public static final class ContextBeans {

        private final Map<String, BeansUtill.BeanDescriptor> beans;

        private final String parentId;

        private ContextBeans(Map<String, BeansUtill.BeanDescriptor> beans, String parentId) {
            this.beans = beans;
            this.parentId = parentId;
        }

        public String getParentId() {
            return this.parentId;
        }

        public Map<String, BeansUtill.BeanDescriptor> getBeans() {
            return this.beans;
        }

        public static BeansUtill.ContextBeans describing(ConfigurableApplicationContext context) {
            if (context == null) {
                return null;
            }
            ConfigurableApplicationContext parent = getConfigurableParent(context);
            return new BeansUtill.ContextBeans(describeBeans(context.getBeanFactory()),
                    (parent != null) ? parent.getId() : null);
        }

        private static Map<String, BeansUtill.BeanDescriptor> describeBeans(
                ConfigurableListableBeanFactory beanFactory) {
            Map<String, BeansUtill.BeanDescriptor> beans = new HashMap<>();
            for (String beanName : beanFactory.getBeanDefinitionNames()) {
                BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
                if (isBeanEligible(beanName, definition, beanFactory)) {
                    beans.put(beanName, describeBean(beanName, definition, beanFactory));
                }
            }
            return beans;
        }

        private static BeansUtill.BeanDescriptor describeBean(String name, BeanDefinition definition,
                                                              ConfigurableListableBeanFactory factory) {
            return new BeansUtill.BeanDescriptor(factory.getAliases(name), definition.getScope(),
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

        private final String scope;
        private final Class<?> type;
        private final String resource;
        private final String[] aliases;
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
