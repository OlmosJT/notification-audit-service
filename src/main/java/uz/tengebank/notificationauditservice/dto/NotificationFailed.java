package uz.tengebank.notificationauditservice.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class NotificationFailed {
    private UUID requestId;
    private String recipientId;
    private String failedStep; // e.g., "template-rendering", "provider-api-call"
    private String reasonCode; // e.g., "TEMPLATE_NOT_FOUND", "INVALID_API_KEY"
    private String details; // A human-readable error message or stack trace summary

}
