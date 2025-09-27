package uz.tengebank.notificationauditservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final class Constants {
        private Constants() {}

        public static final String EXCHANGE_NOTIFICATIONS = "notifications.exchange";
        public static final String QUEUE_EVENTS = "notification.events.queue";
        public static final String QUEUE_EVENTS_DLQ = "notification.events.dlq";
        public static final String ROUTING_KEY_EVENTS = "notification.#";
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(Constants.QUEUE_EVENTS_DLQ);
    }

    /**
     * Defines the main queue for notification events.
     * It's configured to route failed messages to the DLQ by setting the
     * 'x-dead-letter-exchange' and 'x-dead-letter-routing-key' arguments.
     */
    @Bean
    public Queue eventsQueue() {
        return QueueBuilder.durable(Constants.QUEUE_EVENTS)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", Constants.QUEUE_EVENTS_DLQ)
                .build();
    }

    /**
     * Defines the topic exchange that will receive all notification-related events.
     */
    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(Constants.EXCHANGE_NOTIFICATIONS);
    }

    /**
     * Binds the main events queue to the notifications exchange. Any message published
     * to the exchange with a routing key starting with "notification." will be
     * sent to this queue.
     */
    @Bean
    public Binding binding(Queue eventsQueue, TopicExchange notificationsExchange) {
        return BindingBuilder
                .bind(eventsQueue)
                .to(notificationsExchange)
                .with(Constants.ROUTING_KEY_EVENTS);
    }
}
