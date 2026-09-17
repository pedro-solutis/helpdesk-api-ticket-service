package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import java.time.LocalDateTime;

import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

public record TicketListDTO(
    Long id,
    String title,
    Status status,
    Long customerId,
    LocalDateTime createdAt) 
{

    public TicketListDTO(Ticket ticket){
        this(ticket.getId(), ticket.getTitle(), ticket.getStatus(), ticket.getCustomerId(),ticket.getCreatedAt());
    }

}
