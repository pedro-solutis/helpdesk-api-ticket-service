package br.com.solutis.helpdesk.api_ticket_service.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired 
    private TicketService ticketService;

    @PostMapping 
    @Transactional 
    public ResponseEntity<TicketDetailDTO> createTicket(@RequestBody @Valid TicketRegistrationDTO ticketRegistrationDTO, UriComponentsBuilder uriBuilder){
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

    @GetMapping 
    public ResponseEntity<Page<TicketListDTO>> getAllTickets(@PageableDefault(page=0, size = 10, sort = "createdAt") Pageable pageable){
        var tickets = ticketService.getAllTickets(pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDTO> getTicketById(@PathVariable Long id){
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    public void searchTicketByTitle(String title){
        // implement search ticket by title method
    }

    public void filterTickets(String status, String priority, String category){
        // implement filter by status, priority and category
    }
}
