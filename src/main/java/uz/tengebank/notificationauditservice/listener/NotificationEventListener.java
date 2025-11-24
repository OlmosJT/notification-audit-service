package uz.tengebank.notificationauditservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uz.tengebank.notificationauditservice.config.RabbitMQConfig;
import uz.tengebank.notificationauditservice.service.NotificationAuditService;
import uz.tengebank.notificationcontracts.events.EventEnvelope;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final int MAX_RETRIES = 3;
    private static final String RETRY_HEADER = "x-retry-count";
    private static final String DELAY_HEADER = "x-delay";

    private final NotificationAuditService auditService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.Constants.QUEUE_EVENTS)
    public void handleEvent(EventEnvelope event, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            log.info("Received event: type={}, eventId={}", event.getEventType(), event.getEventId());
            auditService.processEvent(event);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            handleFailure(message, channel, tag, event, e);
        }
    }

    private void handleFailure(Message message, Channel channel, long tag, EventEnvelope event, Exception e) throws IOException {
        long retryCount = getRetryCount(message);

        if (retryCount >= MAX_RETRIES) {
            log.error("Max retries ({}) reached for eventId={}. Sending to DLQ.", MAX_RETRIES, event.getEventId(), e);
            channel.basicNack(tag, false, false);
        } else {
            long nextAttempt = retryCount + 1;
            long delay = (long) (Math.pow(5, retryCount) * 1000 * 2);
            log.warn("Error processing eventId={}. Retrying in {}ms (attempt {})...",
                    event.getEventId(), delay, nextAttempt);

            MessageProperties props = message.getMessageProperties();
            props.setHeader(RETRY_HEADER, nextAttempt);
            props.setHeader("x-delay", delay);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.Constants.EXCHANGE_NOTIFICATIONS,
                    props.getReceivedRoutingKey(),
                    message
            );

            channel.basicAck(tag, false);
        }
    }

    /**
     * Gets the current retry count from our custom header.
     */
    private long getRetryCount(Message message) {
        Long count = message.getMessageProperties().getHeader(RETRY_HEADER);
        return (count != null) ? count : 0;
    }
}
