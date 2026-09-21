package br.com.solutis.helpdesk.api_ticket_service.dto.event;

public record TicketEventDTO(
    Long ticketId,
    Long recipientId,
    String eventType,
    String title,
    String message
) {
}

