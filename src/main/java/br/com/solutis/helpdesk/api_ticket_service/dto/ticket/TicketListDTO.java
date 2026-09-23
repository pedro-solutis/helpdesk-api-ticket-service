package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import java.time.LocalDateTime;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

public record TicketListDTO(
    Long id,
    String title,
    String status,
    String priority,
    String category,
    Long customerId,
    LocalDateTime createdAt) 
{

    public TicketListDTO(Ticket ticket){
        this(ticket.getId(), ticket.getTitle(), ticket.getStatus().toString(), ticket.getPriority().toString(), ticket.getCategory().toString(), ticket.getCustomerId(),ticket.getCreatedAt());
    }

}
