package br.com.solutis.helpdesk.api_ticket_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketRegistrationDTO;
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

    public void updateTicket(Long id,Ticket ticket){
        // implement update ticket method
    }

    public void updateTechnician(Long ticketId, Long technicianId){
        // implement update technician method
    }

    public void closeTicket(Long id){
        // implement close ticket method
    }

    public void getTicketByCustomerId(Long customerId){
        // implement get ticket by customer id method
    }

    public void getAllTickets(){
        // implement get all tickets method
    }

    public TicketDetailDTO getTicketById(Long id){
        var ticket = ticketRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return new TicketDetailDTO(ticket.getId(), ticket.getTitle(), ticket.getDescription(), ticket.getStatus(), ticket.getCategory(), ticket.getPriority(), ticket.getCustomerId(), ticket.getTechnicianId());
    }

    public void searchTicketByTitle(String title){
        // implement search ticket by title method
    }

    public void filterTickets(String status, String priority, String category){
        // implement filter by status, priority and category
    }

}
