package br.com.solutis.helpdesk.api_ticket_service.model;

import java.time.LocalDateTime;

public record TicketDetailDTO(
    Long id,
    String title,
    String description,
    Status status,
    Category category,
    Priority priority,
    Long customerId,
    Long technicianId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

    public TicketDetailDTO(Ticket newTicket) {
        this(
            newTicket.getId(),
            newTicket.getTitle(),
            newTicket.getDescription(),
            newTicket.getStatus(),
            newTicket.getCategory(),
            newTicket.getPriority(),
            newTicket.getCustomerId(),
            newTicket.getTechnicianId(),
            newTicket.getCreatedAt(),
            newTicket.getUpdatedAt()
        );
    }

}
