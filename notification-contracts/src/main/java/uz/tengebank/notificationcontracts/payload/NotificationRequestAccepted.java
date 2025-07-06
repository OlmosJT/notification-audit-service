package uz.tengebank.notificationcontracts.payload;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public record NotificationRequestAccepted(
    UUID requestId,
    String source,
    String category,
    String templateName,
    List<String> channels,
    String deliveryStrategy,
    ChannelConfig channelConfig,
    List<Recipient> recipients
) implements Payload {


  public record ChannelConfig(
      Map<String, Object> sms,
      Map<String, Object> push
  ) {}

  public record Recipient(
      String phone,
      String lang,
      Map<String, Object> variables
  ) {}

}
