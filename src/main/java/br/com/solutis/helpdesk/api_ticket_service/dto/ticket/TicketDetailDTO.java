package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import java.time.LocalDateTime;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

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
    LocalDateTime updatedAt,
    boolean active    
) {

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
            newTicket.getUpdatedAt(),
            newTicket.isActive()
        );
    }

}
