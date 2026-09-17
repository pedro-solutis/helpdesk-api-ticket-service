package br.com.solutis.helpdesk.api_ticket_service.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.solutis.helpdesk.api_ticket_service.model.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.model.TicketUpdateDTO;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

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

    @PutMapping("/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> updateTicket(@PathVariable Long id, @RequestBody @Valid TicketUpdateDTO ticket){
        var updatedTicket = ticketService.updateTicket(id, ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @PatchMapping("/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> assignTechnician(@PathVariable("id") Long ticketId, @RequestBody @Valid AssignTechnicianDTO assignTechnician){
        var updatedTechnician = ticketService.assignTechnician(ticketId, assignTechnician);
        return ResponseEntity.ok(updatedTechnician);
    }

    @DeleteMapping("/{id}")
    @Transactional 
    public ResponseEntity<?> closeTicket(@PathVariable Long id){
        ticketService.closeTicket(id);
        return ResponseEntity.ok("Ticket closed with success.");
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Page<TicketListDTO>> searchTicketByCustomerId(@PathVariable("id") Long customerId, Pageable pageable){
        var tickets = ticketService.getAllTicketByCustomerId(customerId, pageable);
        return ResponseEntity.ok(tickets);
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

    @GetMapping ("/search")
    public ResponseEntity<Page<TicketListDTO>> searchTicketByTitle(@RequestParam @NotBlank String title, Pageable pageable){
        var ticket = ticketService.searchTicketByTitle(title, pageable);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<TicketListDTO>> filterTickets(
        @RequestParam(required = false) Status status,
        @RequestParam(required = false) Category category,
        @RequestParam(required = false) Priority priority,
        Pageable pageable){
        var ticket = ticketService.filterTickets(status, category, priority, pageable);
        return  ResponseEntity.ok(ticket);
    }
}
