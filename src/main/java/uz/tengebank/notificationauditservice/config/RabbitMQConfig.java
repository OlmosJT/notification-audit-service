package uz.tengebank.notificationauditservice.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final class Constants {
        private Constants() {
        }

        // --- Main Exchange and Queue ---
        public static final String EXCHANGE_NOTIFICATIONS = "notification.audit.exchange";
        public static final String QUEUE_EVENTS = "notification.events.queue";
        public static final String ROUTING_KEY_EVENTS = "notification.#";

        // --- Dead Letter Exchange and Queue ---
        public static final String EXCHANGE_NOTIFICATIONS_DLX = "notification.audit.exchange.dlx";
        public static final String QUEUE_EVENTS_DLQ = "notification.events.dlq";

    }

    /**
     * Creates a message converter that uses Jackson for JSON (de)serialization.
     * This allows @RabbitListener methods to directly receive Java objects.
     */
    @Bean
    public MessageConverter jackson2MessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("uz.tengebank.notificationcontracts", "uz.tengebank.notificationcontracts.*");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    /**
     * Creates a custom RabbitMQ listener container factory that uses virtual threads
     * and is configured with the JSON message converter.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {

        var factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);

        var executor = new SimpleAsyncTaskExecutor("rabbit-vt-");
        executor.setVirtualThreads(true);
        factory.setTaskExecutor(executor);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    /**
     * The main exchange using the delayed-message plugin. It behaves like a topic exchange.
     */
    @Bean
    public CustomExchange notificationsExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "topic");
        return new CustomExchange(Constants.EXCHANGE_NOTIFICATIONS, "x-delayed-message", true, false, args);
    }

    /**
     * The main queue for notification events. On unrecoverable failure, it will
     * dead-letter messages to the dedicated DLX (Dead Letter Exchange).
     */
    @Bean
    public Queue eventsQueue() {
        return QueueBuilder.durable(Constants.QUEUE_EVENTS)
                .withArgument("x-dead-letter-exchange", Constants.EXCHANGE_NOTIFICATIONS_DLX)
                .build();
    }

    /**
     * The final queue where messages that fail all processing attempts are sent.
     */
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(Constants.QUEUE_EVENTS_DLQ);
    }



    /**
     * The Dead Letter Exchange (DLX). A simple Fanout exchange is perfect for this,
     * as it will broadcast any message it receives to all queues bound to it (our DLQ).
     */
    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(Constants.EXCHANGE_NOTIFICATIONS_DLX);
    }

    /**
     * Binds the main events queue to the main notifications exchange.
     */
    @Bean
    public Binding binding(Queue eventsQueue, CustomExchange notificationsExchange) {
        return BindingBuilder
                .bind(eventsQueue)
                .to(notificationsExchange)
                .with(Constants.ROUTING_KEY_EVENTS)
                .noargs();
    }

    /**
     * Binds the dead-letter queue to the dead-letter exchange.
     */
    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, FanoutExchange deadLetterExchange) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange);
    }
}
