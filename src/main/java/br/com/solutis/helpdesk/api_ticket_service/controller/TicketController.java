package br.com.solutis.helpdesk.api_ticket_service.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.*;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired 
    private TicketService ticketService;

    @PreAuthorize (value = "hasRole('CLIENT')")
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

    @PreAuthorize (value = "hasRole('ADMIN')")
    @PatchMapping("/technician/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> assignTechnician(@PathVariable("id") Long ticketId, @RequestBody @Valid AssignTechnicianDTO assignTechnician){
        var updatedTechnician = ticketService.assignTechnician(ticketId, assignTechnician);
        return ResponseEntity.ok(updatedTechnician);
    }

    @PreAuthorize (value = "hasAnyRole('CLIENT', 'ADMIN')")
    @PatchMapping("/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> closeTicket(@PathVariable("id") Long ticketId){
        var ticket = ticketService.closeTicket(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping 
    public ResponseEntity<Page<TicketListDTO>> getAllTickets(
        @RequestParam (required = false) Long customerId,
        @RequestParam (required = false) Long technicianId,
        @RequestParam (required = false) String title,
        @RequestParam (required = false) String status,
        @RequestParam (required = false) String category,
        @RequestParam (required = false) String priority,
        @PageableDefault(page=0, size = 10, sort = "createdAt"  
        ) Pageable pageable){
        var tickets = ticketService.getAllTickets(customerId, technicianId, title, status, category, priority, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDTO> getTicketById(@PathVariable Long id){
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics(Authentication auth){
        var metricsDTO = ticketService.getDashboardMetrics(auth);
        return ResponseEntity.ok(metricsDTO);
    }

    @PreAuthorize (value = "hasAnyRole('CLIENT', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable("id") Long ticketId){
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
