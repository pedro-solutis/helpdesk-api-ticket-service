package br.com.solutis.helpdesk.api_ticket_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketUpdateDTO;
import br.com.solutis.helpdesk.api_ticket_service.infra.amqp.TicketEventProducer;
import br.com.solutis.helpdesk.api_ticket_service.infra.exception.ResourceNotFoundException;
import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.repository.TicketRepository;
import br.com.solutis.helpdesk.api_ticket_service.validation.CustomerValidator;
import br.com.solutis.helpdesk.api_ticket_service.validation.TechnicianValidator;

@Service 
public class TicketService {

    @Autowired 
    private TicketRepository ticketRepository;

    @Autowired 
    private CustomerValidator customerValidator;

    @Autowired 
    private TechnicianValidator technicianValidator;

    @Autowired
    private TicketEventProducer ticketEventProducer;

    public TicketDetailDTO createTicket(TicketRegistrationDTO ticketRegistrationDTO){
        if(!customerValidator.userExist(ticketRegistrationDTO.customerId()))
            throw new IllegalArgumentException("This user can not be assigned as a customer");
        var newTicket = new Ticket(ticketRegistrationDTO);
        ticketRepository.save(newTicket);
        ticketEventProducer.ticketCreatedEvent(newTicket);
        return new TicketDetailDTO(newTicket);
    }

    public TicketDetailDTO updateTicket(Long id,TicketUpdateDTO ticketDto){
        var toUpdateTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(ticketDto.status() != null && toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible change status for a closed ticket.");
        var lastStatus = toUpdateTicket.getStatus();
        toUpdateTicket.updateTicket(ticketDto);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        if (ticketDto.status() != null)
            ticketEventProducer.ticketStatusChangedEvent(updatedTicket, lastStatus);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO assignTechnician(Long ticketId, AssignTechnicianDTO assignTechnicianDTO){
        if (!technicianValidator.userExist(assignTechnicianDTO.technicianId()))
            throw new IllegalArgumentException("This user can not be assigned as a technician");
        var toUpdateTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible assign a technician for a closed ticket.");
        toUpdateTicket.assignTechnician(assignTechnicianDTO);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        ticketEventProducer.ticketAssignedEvent(updatedTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO closeTicket(Long id){
        var toCloseTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        var lastStatus = toCloseTicket.getStatus();
        toCloseTicket.closeTicket();
        var closedTicket = ticketRepository.save(toCloseTicket);
        ticketEventProducer.ticketStatusChangedEvent(closedTicket, lastStatus);
        return new TicketDetailDTO(closedTicket);
    }

    public Page<TicketListDTO> getAllTicketByCustomerId(Long customerId, Pageable pageable){
        var tickets = ticketRepository.findAllByCustomerId(customerId, pageable);
        return tickets.map(TicketListDTO::new);
    }

    public Page<TicketListDTO> getAllTickets(Pageable pageable){
        var tickets = ticketRepository.findAll(pageable);
        return tickets.map(TicketListDTO::new);
    }

    public TicketDetailDTO getTicketById(Long id){
        var ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return new TicketDetailDTO(ticket);
    }

    public Page<TicketListDTO> searchTicketByTitle(String title, Pageable pageable){
        var tickets = ticketRepository.findByTitleContainingIgnoreCase(title, pageable);
        return tickets.map(TicketListDTO::new);
    }

    public Page<TicketListDTO> filterTickets(Status status, Category category, Priority priority, Pageable pageable){
        var tickets = ticketRepository.filterTickets(status, category, priority, pageable);
        return tickets.map(TicketListDTO::new);
    }

    public void deleteTicket(Long ticketId) {
        var toDeleteTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not founded"));
        toDeleteTicket.deleteTicket();
        var lastStatus = toDeleteTicket.getStatus();
        toDeleteTicket.closeTicket();
        ticketRepository.save(toDeleteTicket);
        ticketEventProducer.ticketStatusChangedEvent(toDeleteTicket, lastStatus);
    }

}
