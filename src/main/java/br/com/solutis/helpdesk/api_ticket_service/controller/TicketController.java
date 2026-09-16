package br.com.solutis.helpdesk.api_ticket_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired 
    private TicketService ticketService;

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

    public void searchTicketByCustomerId(Long customerId){
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
