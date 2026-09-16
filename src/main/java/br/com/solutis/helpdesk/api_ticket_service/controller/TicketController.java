package br.com.solutis.helpdesk.api_ticket_service.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired 
    private TicketService ticketService;

    @PostMapping 
    @Transactional 
    public ResponseEntity<TicketDetailDTO> createTicket(TicketRegistrationDTO ticketRegistrationDTO, UriComponentsBuilder uriBuilder){
        TicketDetailDTO createdTicket = ticketService.createTicket(ticketRegistrationDTO);
        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(createdTicket.id()).toUri();        
        return ResponseEntity.created(uri).body(createdTicket);
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
