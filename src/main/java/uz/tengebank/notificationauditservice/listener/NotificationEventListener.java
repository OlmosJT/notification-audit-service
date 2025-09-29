package uz.tengebank.notificationauditservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import uz.tengebank.notificationauditservice.config.RabbitMQConfig;
import uz.tengebank.notificationauditservice.service.NotificationAuditService;
import uz.tengebank.notificationcontracts.events.EventEnvelope;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private static final int MAX_RETRIES = 3;
    private static final String RETRY_HEADER = "x-retry-count";

    private final NotificationAuditService auditService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.Constants.QUEUE_EVENTS)
    public void handleEvent(Message message, EventEnvelope event) {
        log.info("Received event: type={}, eventId={}", event.getEventType(), event.getEventId());
        try {
            auditService.processEvent(event);
        } catch (Exception e) {
            long retryCount = getRetryCount(message);

            if (retryCount >= MAX_RETRIES) {
                log.error("Max retries ({}) reached for eventId={}. Sending to DLQ.", MAX_RETRIES, event.getEventId(), e);
                throw new AmqpRejectAndDontRequeueException("Max retries reached", e);
            } else {
                long nextAttempt = retryCount + 1;
                long delay = (long) (Math.pow(5, retryCount) * 1000 * 2);
                log.warn("Error processing eventId={}. Retrying in {}ms (attempt {})...",
                        event.getEventId(), delay, nextAttempt);

                // FIX: Set our custom retry header and the delay header
                MessageProperties props = message.getMessageProperties();
                props.setHeader(RETRY_HEADER, nextAttempt);
                props.setHeader("x-delay", delay);

                // Re-publish the modified message to the delayed exchange
                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.Constants.EXCHANGE_NOTIFICATIONS,
                        props.getReceivedRoutingKey(),
                        message
                );
            }
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
