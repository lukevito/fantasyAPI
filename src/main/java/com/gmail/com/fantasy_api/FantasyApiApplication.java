package com.gmail.com.fantasy_api;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(Registrar.class)
public class FantasyApiApplication {

    /**
     * Na tym etapie aby odpalic aplikacje potrzebny jest rabbit i neo4j na defaultowych ustawienieach + applicationl.properties dla neo4j configuration api
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(FantasyApiApplication.class, args);
    }

    static final String topicExchangeName = "spring-boot-graph";
    static final String queueVisName = "spring-boot-vis";

    @Bean
    Queue queue() {
        return new Queue(queueVisName, true, false,false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName, true, false);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }


    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueVisName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }


    @Bean
    public static AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exp = new AutoJsonRpcServiceImplExporter();
        //in here you can provide custom HTTP status code providers etc. eg:
        //exp.setHttpStatusCodeProvider();
        //exp.setErrorResolver();
        return exp;
    }
}
