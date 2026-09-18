package br.com.solutis.helpdesk.api_ticket_service.dto.event;

import java.time.LocalDateTime;

import br.com.solutis.helpdesk.api_ticket_service.model.Status;

public record TicketEventDTO(
    Long id,
    String title,
    String description,
    Status status,
    Long customerId,
    Long technicianId,
    LocalDateTime createdAt,
    String eventType
) {
}

