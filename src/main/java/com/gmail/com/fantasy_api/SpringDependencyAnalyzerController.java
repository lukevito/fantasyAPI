package com.gmail.com.fantasy_api;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Spring dependency analyzer that works with any GenericApplicationContext.
 */
@RestController
public class SpringDependencyAnalyzerController {

    private final GenericApplicationContext context;

    /**
     * @param context create your spring context the usual way and inject it here.
     */
    @Autowired
    public SpringDependencyAnalyzerController(GenericApplicationContext context) {
        this.context = context;
    }

    /**
     * Long lists of dependencies indicate low cohesiveness and high coupling. This helps you identify the problematic beans.
     * @return map of dependencies for all beans in the context
     */
    @GetMapping("/bean-dependencies")
    public JSONArray getBeanDependenciesApi() {
        JSONObject returnJson =  new JSONObject();

        Map<String, Set<String>> beanDependencies = getBeanDependencies();
        beanDependencies.forEach((beanName, dependencies)-> {
            JSONArray jsonArray = (JSONArray) returnJson.get(beanName);
            if(jsonArray == null) {
                jsonArray = new JSONArray();
                returnJson.put(beanName, getJsonObject("dependencies", jsonArray));
            }
            for(String dependency : dependencies) {
                jsonArray.add(dependency);
            }
        });
        JSONArray beanArray = new JSONArray();
        returnJson.forEach((s, o) -> {
            JSONObject bean = new JSONObject();
            bean.put("name", s);
            JSONObject ds = ((JSONObject)o);
            JSONArray dependencies = (JSONArray) ds.get("dependencies");
            if (dependencies.size() > 0) {
                bean.merge(o);
            }
            beanArray.add(bean);
        });

        return beanArray;
    }

    private JSONObject getJsonObject( String key, JSONArray jsonArray) {
        JSONObject jbean = new JSONObject();
        jbean.put(key, jsonArray);
        return jbean;
    }
    private JSONObject getJsonObject( String key, JSONObject jsonArray) {
        JSONObject jbean = new JSONObject();
        jbean.put(key, jsonArray);
        return jbean;
    }

    //https://stackoverflow.com/questions/1088550/how-to-call-a-method-after-bean-initialization-is-complete
    //https://www.baeldung.com/circular-dependencies-in-spring
    //https://docs.spring.io/spring-boot/docs/2.0.x/actuator-api/html/
    public Map<String, Set<String>> getBeanDependencies() {
        Map<String, Set<String>> beanDeps = new TreeMap<>();
        ConfigurableListableBeanFactory factory = context.getBeanFactory();
        for(String beanName : factory.getBeanDefinitionNames()) {
            if(factory.getBeanDefinition(beanName).isAbstract()) {
                continue;
            }
            String[] dependenciesForBean = factory.getDependenciesForBean(beanName);
            Set<String> set = beanDeps.get(beanName);
            if(set == null) {
                set = new TreeSet<>();
                beanDeps.put(beanName, set);
            }
            for(String dependency : dependenciesForBean) {
                set.add(dependency);
            }
        }
        return beanDeps;
    }

    /**
     * If you have a lot of beans that are not depended on or only once, maybe they shouldn't be a bean at all.
     * @return map of reverse dependencies for all beans in the context
     */
    public Map<String, Set<String>> getReverseBeanDependencies() {
        Map<String, Set<String>> reverseBeanDeps = new TreeMap<>();
        Map<String, Set<String>> beanDeps = getBeanDependencies();

        beanDeps.forEach((beanName,deps) -> {
            for(String dep: deps) {
                Set<String> set = reverseBeanDeps.get(dep);
                if(set == null) {
                    set = new TreeSet<>();
                    reverseBeanDeps.put(dep, set);
                }
                set.add(beanName);
            }
        });

        return reverseBeanDeps;
    }

    /**
     * Organizes the graph of configuration classes in layers that depend on each other.
     * Classes in the same layer can only import classes in lower layers. Spring does not allow import cycles.
     * A good pattern is to have a RootConfig for your application that simply imports everything else you need.
     * The more layers you have the more complex your dependencies.
     * @param configurationClass the root configuration class that you want to analyze
     * @return treemap with layers of configuratino
     */
    public Map<Integer, Set<Class<?>>> getConfigurationLayers(Class<?> configurationClass) {

        SimpleGraph<Class<?>> rootGraph = getConfigurationGraph(configurationClass);
        return rootGraph.getLayers();
    }

    private void validateIsConfigurationClass(Class<?> configurationClass) {
        boolean isConfigClass = false;
        for(Annotation annotation : configurationClass.getAnnotations()) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(Configuration.class.equals(type)) {
                isConfigClass = true;
            }
        }
        if(!isConfigClass) {
            throw new IllegalArgumentException("not a spring configuration class");
        }
    }

    /**
     * @param configurationClass spring configuration root class from which to calculate the configuration hierarchy
     * @return a graph of the configuration classes
     */
    public SimpleGraph<Class<?>> getConfigurationGraph(Class<?> configurationClass) {
        validateIsConfigurationClass(configurationClass);
        return SimpleGraph.treeBuilder(configurationClass, SpringDependencyAnalyzerController::getConfigurationImportsFor);
    }

    public SimpleGraph<String> getBeanGraph() {
        Map<String, Set<String>> beanDeps = getBeanDependencies();
        Map<String, Set<String>> reverseBeanDeps = getReverseBeanDependencies();

        SimpleGraph<String> graph = new SimpleGraph<>();

        beanDeps.forEach((bean,deps) -> {
            if(deps.isEmpty()) {
                // bean has no deps, so we can figure out everything that depends on this bean here
                SimpleGraph<String> depGraph = new SimpleGraph<>();
                SimpleGraph.buildGraph(depGraph,bean, b -> reverseBeanDeps.get(b));
                graph.put(bean, depGraph);
            }
        });
        // FIXME technically this is a reverse dependency graph, We need to revert it.
        return graph;
    }

    private static List<Class<?>> getConfigurationImportsFor(Class<?> clazz) {
        List<Class<?>> list= new ArrayList<>();
        for(Annotation annotation : clazz.getAnnotations()) {
            Class<? extends Annotation> type = annotation.annotationType();
            if(Import.class.equals(type)) {
                try {
                    Method method = type.getMethod("value");
                    Class<?>[] imports = (Class<?>[]) method.invoke(annotation, (Object[]) null);
                    if(imports != null && imports.length > 0) {
                        for(Class<?> c: imports) {
                            list.add(c);
                        }
                    }
                } catch (Throwable e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        return list;
    }

    public String configurationGraphCypher(Class<?> rootClass) {
        return getConfigurationGraph(rootClass).toCypher("ConfigClass", "Imports", c->c.getSimpleName());
    }

    public String beanGraphCypher() {
        return getBeanGraph().toCypher("Bean", "DEPENDSON", s->s.replace(".", "_"));
    }

    public void printReport(Class<?> springConfigurationClass) {
        System.err.println("Configuration layers:\n");
        getConfigurationLayers(springConfigurationClass).forEach((layer,classes) -> {
            System.err.println("" + layer + "\t" + StringUtils.join(classes,','));
        });

        System.err.println("\n\nDependencies:\n");
        Map<String, Set<String>> beanDependencies = getBeanDependencies();
        beanDependencies.forEach((name,dependencies) -> {
            System.err.println(name + ": " + StringUtils.join(dependencies,','));
        });
        System.err.println("\n\nReverse dependencies:\n");
        Map<String, Set<String>> reverseBeanDependencies = getReverseBeanDependencies();
        reverseBeanDependencies.forEach((name,dependencies) -> {
            System.err.println(name + ": " + StringUtils.join(dependencies,','));
        });

        System.err.println("\n\nBean dependency graph:\n");
        System.err.println(getBeanGraph());
        System.err.println("Bean layers:\n");

        getBeanGraph().getLayers().forEach((layer,classes) -> {
            System.err.println("" + layer + "\t" + StringUtils.join(classes,','));
        });
    }
}
