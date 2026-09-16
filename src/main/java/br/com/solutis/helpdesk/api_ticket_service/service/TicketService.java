package br.com.solutis.helpdesk.api_ticket_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.repository.TicketRepository;

@Service 
public class TicketService {

    @Autowired 
    private TicketRepository ticketRepository;

    public void createTicket(Ticket ticket){
        // implement create ticket method
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

    public void getTicketById(){
        // implement get ticket by id method
    }

    public void searchTicketByTitle(String title){
        // implement search ticket by title method
    }

    public void filterTickets(String status, String priority, String category){
        // implement filter by status, priority and category
    }

}
