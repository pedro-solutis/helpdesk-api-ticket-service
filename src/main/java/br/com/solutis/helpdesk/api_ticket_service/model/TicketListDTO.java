package br.com.solutis.helpdesk.api_ticket_service.model;

import java.time.LocalDateTime;

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
