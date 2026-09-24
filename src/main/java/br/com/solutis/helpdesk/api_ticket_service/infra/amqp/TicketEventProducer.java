package br.com.solutis.helpdesk.api_ticket_service.infra.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.solutis.helpdesk.api_ticket_service.dto.event.TicketEventDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

@Component
public class TicketEventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${api.messager.exchange}")
    private String exchangeName;

    public TicketEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void ticketCreatedEvent(Ticket ticket) {
        TicketEventDTO event = createEventCreatedTicket(ticket, "ticketCreated");
        rabbitTemplate.convertAndSend(exchangeName, "ticket.created", event);
    }

    public void ticketAssignedEvent(Ticket ticket) {
        TicketEventDTO event = createEventAssignedTicket(ticket, "ticketAssigned");
        rabbitTemplate.convertAndSend(exchangeName, "ticket.assigned", event);
    }

    public void ticketStatusChangedEvent(Ticket ticket, Status lastStatus) {
        TicketEventDTO event = createEventStatusChangedTicket(ticket, lastStatus, "ticketStatusChanged");
        rabbitTemplate.convertAndSend(exchangeName, "ticket.status.changed", event);
    }

    public void ticketUpdatedEvent(Ticket ticket) {
        TicketEventDTO event = createEventTicketUpdated(ticket, "ticketUpdated");
        rabbitTemplate.convertAndSend(exchangeName, "ticket.updated", event);
    }

    public void ticketDeletedEvent(Ticket ticket) {
        TicketEventDTO event = createEventTicketDeleted(ticket, "ticketDeletion");
        rabbitTemplate.convertAndSend(exchangeName, "ticket.deleted", event);
    }

    private TicketEventDTO createEventCreatedTicket(Ticket ticket, String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket criado pelo cliente " + ticket.getCustomerId()
        );
    }

    private TicketEventDTO createEventAssignedTicket(Ticket ticket, String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getTechnicianId(),
            eventType,
            ticket.getTitle(),
            "Ticket atribuido para o técnico " + ticket.getTechnicianId()
        );
    }

    private TicketEventDTO createEventStatusChangedTicket(Ticket ticket, Status lastStatus,String eventType) {
        return new TicketEventDTO(
            ticket.getId(),
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket status alterado de " + lastStatus + " para "+ ticket.getStatus()
        );
    }

    private TicketEventDTO createEventTicketUpdated(Ticket ticket, String eventType){
        return  new TicketEventDTO(
            ticket.getId(), 
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket atualizado com sucesso"
        );
    }

    private TicketEventDTO createEventTicketDeleted(Ticket ticket, String eventType){
        return  new TicketEventDTO(
            ticket.getId(), 
            ticket.getCustomerId(),
            eventType,
            ticket.getTitle(),
            "Ticket deletado com sucesso"
        );
    }
}
