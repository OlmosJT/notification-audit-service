package uz.tengebank.notificationauditservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import uz.tengebank.notificationauditservice.config.RabbitMQConfig;
import uz.tengebank.notificationauditservice.service.NotificationAuditService;
import uz.tengebank.notificationcontracts.events.EventEnvelope;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationAuditService auditService;

    @RabbitListener(queues = RabbitMQConfig.Constants.QUEUE_EVENTS)
    public void handleEvent(EventEnvelope event) {
        log.info("Received event: type={}, eventId={}", event.getEventType(), event.getEventId());
        try {
            auditService.processEvent(event);
        } catch (Exception e) {
            log.error("Unrecoverable error processing eventId={}. Sending to DLQ. Error: {}",
                    event.getEventId(), e.getMessage(), e);
            // This exception tells RabbitMQ to not re-queue the message and instead send it to the DLQ.
            throw new AmqpRejectAndDontRequeueException("Error processing event", e);
        }
    }
}
