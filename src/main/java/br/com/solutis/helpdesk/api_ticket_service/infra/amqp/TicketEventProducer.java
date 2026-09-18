package br.com.solutis.helpdesk.api_ticket_service.infra.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import br.com.solutis.helpdesk.api_ticket_service.dto.event.TicketEventDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.event.TicketStatusDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

@Component
public class TicketEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public TicketEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void ticketCreatedEvent(Ticket ticket) {
        TicketEventDTO event = createEvent(ticket, "ticketCreated");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.created", event);
    }

    public void ticketAssignedEvent(Ticket ticket) {
        TicketEventDTO event = createEvent(ticket, "ticketAssigned");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.assigned", event);
    }

    public void ticketStatusChangedEvent(Ticket ticket, Status lastStatus) {
        TicketStatusDTO event = createEvent(ticket, lastStatus, "ticketStatusChanged");
        rabbitTemplate.convertAndSend(TicketAMQPConfiguration.EXCHANGE_NAME, "ticket.status.changed", event);
    }

    private TicketEventDTO createEvent(Ticket ticket, String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            ticket.getStatus(),
            ticket.getCustomerId(),
            ticket.getTechnicianId(),
            ticket.getCreatedAt(),
            eventType
        );
    }

    private TicketStatusDTO createEvent(Ticket ticket, Status lastStatus, String eventType){
        return new TicketStatusDTO(
            ticket.getId(),
            ticket.getTitle(),
            ticket.getDescription(),
            lastStatus,
            ticket.getStatus(),
            ticket.getCustomerId(),
            ticket.getTechnicianId(),
            ticket.getCreatedAt(),
            eventType
        );
    }
}
