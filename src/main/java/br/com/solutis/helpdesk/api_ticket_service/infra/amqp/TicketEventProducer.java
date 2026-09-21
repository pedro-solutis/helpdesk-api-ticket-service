package br.com.solutis.helpdesk.api_ticket_service.infra.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.com.solutis.helpdesk.api_ticket_service.dto.event.TicketEventDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

@Component
public class TicketEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public TicketEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void ticketCreatedEvent(Ticket ticket) {
        TicketEventDTO event = createEventCreatedTicket(ticket, "ticketCreated");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.created", event);
    }

    public void ticketAssignedEvent(Ticket ticket) {
        TicketEventDTO event = createEventAssignedTicket(ticket, "ticketAssigned");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.assigned", event);
    }

    public void ticketStatusChangedEvent(Ticket ticket, Status lastStatus) {
        TicketEventDTO event = createEventStatusChangedTicket(ticket, lastStatus, "ticketStatusChanged");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.status.changed", event);
    }

    private TicketEventDTO createEventCreatedTicket(Ticket ticket, String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket: " + ticket.getTitle() + " was created by user " + ticket.getTechnicianId()
        );
    }

    private TicketEventDTO createEventAssignedTicket(Ticket ticket, String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getTechnicianId(),
            eventType,
            ticket.getTitle(),
            "Ticket: " + ticket.getTitle() + " was assigned to user " + ticket.getTechnicianId()
        );
    }

    private TicketEventDTO createEventStatusChangedTicket(Ticket ticket, Status lastStatus,String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket: " + ticket.getTitle() + " status changed from " + lastStatus + " to "+ ticket.getStatus()
        );
    }
}
