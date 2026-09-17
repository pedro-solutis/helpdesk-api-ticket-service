package br.com.solutis.helpdesk.api_ticket_service.infra.exception;

import java.time.LocalDateTime;
import java.util.List;

public record StandardError(
    LocalDateTime timestamp,
    Integer status,
    String error,
    String message,
    String path,
    List<FieldError> validationErrors
) {
    public record FieldError(String field, String message) {}
}

