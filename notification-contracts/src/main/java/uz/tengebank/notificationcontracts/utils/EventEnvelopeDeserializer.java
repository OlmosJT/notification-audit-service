package uz.tengebank.notificationcontracts.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uz.tengebank.notificationcontracts.events.EventEnvelope;
import uz.tengebank.notificationcontracts.events.EventType;
import uz.tengebank.notificationcontracts.payload.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventEnvelopeDeserializer extends StdDeserializer<EventEnvelope> {


    private static final Map<String, Class<? extends Payload>> PAYLOAD_TYPE_MAP = Stream.of(new Object[][]{
            {EventType.NOTIFICATION_REQUEST_ACCEPTED_V1, NotificationRequestAccepted.class},
            {EventType.NOTIFICATION_REQUEST_REJECTED_V1, NotificationRequestRejected.class},
            {EventType.NOTIFICATION_REQUEST_PROCESSING_V1, NotificationRequestProcessing.class},
            {EventType.NOTIFICATION_REQUEST_FAILED_V1, NotificationRequestFailed.class},
            {EventType.NOTIFICATION_REQUEST_COMPLETED_V1, NotificationRequestCompleted.class},
            {EventType.NOTIFICATION_REQUEST_PARTIALLY_COMPLETED_V1, NotificationRequestPartiallyCompleted.class},
            {EventType.INDIVIDUAL_NOTIFICATION_ACCEPTED_V1, IndividualNotificationAccepted.class},
            {EventType.INDIVIDUAL_NOTIFICATION_ROUTED_V1, IndividualNotificationRouted.class},
            {EventType.INDIVIDUAL_NOTIFICATION_DISPATCHED_V1, IndividualNotificationDispatched.class},
            {EventType.INDIVIDUAL_NOTIFICATION_DELIVERED_V1, IndividualNotificationDelivered.class},
            {EventType.INDIVIDUAL_NOTIFICATION_DELIVERY_FAILED_V1, IndividualNotificationDeliveryFailed.class},
            {EventType.INDIVIDUAL_NOTIFICATION_INTERNAL_FAILURE_V1, IndividualNotificationInternalFailure.class},
            {EventType.INDIVIDUAL_NOTIFICATION_READ_V1, IndividualNotificationRead.class}
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (Class<? extends Payload>) data[1]));


    public EventEnvelopeDeserializer() {
        this(null);
    }

    protected EventEnvelopeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public EventEnvelope deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode rootNode = mapper.readTree(jp);

        // 1. Read all the top-level fields from the envelope
        String eventType = rootNode.get("eventType").asText();
        JsonNode payloadNode = rootNode.get("payload");

        // 2. Look up the correct payload class based on the eventType
        Class<? extends Payload> payloadClass = PAYLOAD_TYPE_MAP.get(eventType);
        if (payloadClass == null) {
            throw new IOException("Unknown eventType: " + eventType);
        }

        // 3. Deserialize the payload node into the specific, correct class
        Payload payload = mapper.treeToValue(payloadNode, payloadClass);

        // 4. Build and return the final EventEnvelope object
        EventEnvelope envelope = new EventEnvelope();
        envelope.setEventId(UUID.fromString(rootNode.get("eventId").asText()));
        envelope.setEventType(eventType);
        envelope.setVersion(rootNode.get("version").asText());
        envelope.setSourceService(rootNode.get("sourceService").asText());
        envelope.setEventTimestamp(OffsetDateTime.parse(rootNode.get("eventTimestamp").asText()));
        envelope.setPayload(payload);

        return envelope;
    }
}
