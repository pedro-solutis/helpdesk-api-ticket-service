package br.com.solutis.helpdesk.api_ticket_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.solutis.helpdesk.api_ticket_service.infra.exception.ResourceNotFoundException;
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

    public TicketDetailDTO updateTicket(Long id,TicketUpdateDTO ticketDto){
        var toUpdateTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(ticketDto.status() != null && toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible change status for a closed ticket.");
        toUpdateTicket.updateTicket(ticketDto);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO assignTechnician(Long ticketId, AssignTechnicianDTO assignTechnicianDTO){
        var toUpdateTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible assign a technician for a closed ticket.");
        toUpdateTicket.assignTechnician(assignTechnicianDTO);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public void closeTicket(Long id){
        var toCloseTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        toCloseTicket.closeTicket();
        ticketRepository.save(toCloseTicket);
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

}
