package br.com.solutis.helpdesk.api_ticket_service.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.solutis.helpdesk.api_ticket_service.model.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketUpdateDTO;
import br.com.solutis.helpdesk.api_ticket_service.repository.TicketRepository;

@Service 
public class TicketService {

    @Autowired 
    private TicketRepository ticketRepository;

    public TicketDetailDTO createTicket(TicketRegistrationDTO ticketRegistrationDTO){
        var newTicket = new Ticket(ticketRegistrationDTO);
        ticketRepository.save(newTicket);
        return new TicketDetailDTO(newTicket);
    }

    public TicketDetailDTO updateTicket(Long id,TicketUpdateDTO ticket){
        var toUpdateTicket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if(ticket.category() != null)
            toUpdateTicket.setCategory(ticket.category());
        if(ticket.priority() != null)
            toUpdateTicket.setPriority(ticket.priority());
        if (ticket.status() != null)
            toUpdateTicket.setStatus(ticket.status());
        if(ticket.description() != null)
            toUpdateTicket.setDescription(ticket.description());
        toUpdateTicket.setUpdatedAt(LocalDateTime.now());
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO assignTechnician(Long ticketId, AssignTechnicianDTO assignTechnician){
        var toUpdateTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if(toUpdateTicket != null)
            toUpdateTicket.setTechnicianId(assignTechnician.technicianId());
        toUpdateTicket.setUpdatedAt(LocalDateTime.now());
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public void closeTicket(Long id){
        var toCloseTicket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        toCloseTicket.closeTicket();
        ticketRepository.save(toCloseTicket);
    }

    public TicketDetailDTO getTicketByCustomerId(Long customerId){
        var ticket = ticketRepository.findByCustomerId(customerId);
        return new TicketDetailDTO(ticket);
    }

    public Page<TicketListDTO> getAllTickets(Pageable pageable){
        var tickets = ticketRepository.findAll(pageable);
        return tickets.map(TicketListDTO::new);
    }

    public TicketDetailDTO getTicketById(Long id){
        var ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return new TicketDetailDTO(ticket);
    }

    public TicketDetailDTO searchTicketByTitle(String title){
        var ticket = ticketRepository.findByTitleContainingIgnoreCase(title);
        return new TicketDetailDTO(ticket);
    }

    public Page<TicketListDTO> filterTickets(Status status, Category category, Priority priority, Pageable pageable){
        var tickets = ticketRepository.filterTickets(status, category, priority, pageable);
        return tickets.map(TicketListDTO::new);
    }

}
