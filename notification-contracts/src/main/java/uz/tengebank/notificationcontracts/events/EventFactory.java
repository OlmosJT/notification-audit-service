package uz.tengebank.notificationcontracts.events;

import uz.tengebank.notificationcontracts.payload.Payload;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A helper factory for creating EventEnvelope instances consistently.
 * This centralizes the logic for populating common event metadata.
 */
public class EventFactory {

  private static final String DEFAULT_VERSION = "1.0";

  private EventFactory() {}

  /**
   * Creates a new EventEnvelope using the default version ("1.0").
   *
   * @param payload       The event payload.
   * @param eventType     The type of the event (from EventType constants).
   * @param sourceService The name of the publishing microservice.
   * @return A fully populated EventEnvelope.
   */
  public static EventEnvelope create(Payload payload, String eventType, String sourceService) {
    return create(payload, eventType, sourceService, DEFAULT_VERSION);
  }

  /**
   * Creates a new EventEnvelope with a specific version.
   *
   * @param payload       The event payload.
   * @param eventType     The type of the event (from EventType constants).
   * @param sourceService The name of the publishing microservice.
   * @param version       The specific version of the event schema.
   * @return A fully populated EventEnvelope.
   */
  public static EventEnvelope create(Payload payload, String eventType, String sourceService, String version) {
    EventEnvelope envelope = new EventEnvelope();
    envelope.setEventId(UUID.randomUUID());
    envelope.setEventType(eventType);
    envelope.setVersion(version);
    envelope.setSourceService(sourceService);
    envelope.setEventTimestamp(OffsetDateTime.now());
    envelope.setPayload(payload);
    return envelope;
  }
}
