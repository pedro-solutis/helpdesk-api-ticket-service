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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/tickets")
@Tag(name = "Tickets", description = "Endpoints for ticket management")
@SecurityRequirement(name = "bearer-key")
public class TicketController {

    @Autowired 
    private TicketService ticketService;

    @Operation(summary = "Create a new ticket", description = "Creates a new ticket in the system. Only users with the CLIENT role can perform this operation.")
    @ApiResponse(responseCode = "201", description = "Ticket created successfully")
    @PreAuthorize (value = "hasRole('CLIENT')")
    @PostMapping 
    @Transactional 
    public ResponseEntity<TicketDetailDTO> createTicket(@RequestBody @Valid TicketRegistrationDTO ticketRegistrationDTO, UriComponentsBuilder uriBuilder){
        TicketDetailDTO createdTicket = ticketService.createTicket(ticketRegistrationDTO);
        URI uri = uriBuilder.path("/tickets/{id}").buildAndExpand(createdTicket.id()).toUri();        
        return ResponseEntity.created(uri).body(createdTicket);
    }

    @Operation(summary = "Update a ticket", description = "Updates the details of an existing ticket.")
    @ApiResponse(responseCode = "200", description = "Ticket updated successfully")
    @PutMapping("/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> updateTicket(
            @Parameter(description = "ID of the ticket to be updated") @PathVariable Long id, 
            @RequestBody @Valid TicketUpdateDTO ticket){
        var updatedTicket = ticketService.updateTicket(id, ticket);
        return ResponseEntity.ok(updatedTicket);
    }

    @Operation(summary = "Assign a technician", description = "Assigns a technician to a ticket. Only administrators (ADMIN) can perform this operation.")
    @ApiResponse(responseCode = "200", description = "Technician assigned successfully")
    @PreAuthorize (value = "hasRole('ADMIN')")
    @PatchMapping("/technician/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> assignTechnician(
            @Parameter(description = "ID of the ticket") @PathVariable("id") Long ticketId, 
            @RequestBody @Valid AssignTechnicianDTO assignTechnician){
        var updatedTechnician = ticketService.assignTechnician(ticketId, assignTechnician);
        return ResponseEntity.ok(updatedTechnician);
    }

    @Operation(summary = "Close a ticket", description = "Closes a ticket that is already resolved. Only CLIENT or ADMIN roles can perform this operation.")
    @ApiResponse(responseCode = "200", description = "Ticket closed successfully")
    @PreAuthorize (value = "hasAnyRole('CLIENT', 'ADMIN')")
    @PatchMapping("/{id}")
    @Transactional 
    public ResponseEntity<TicketDetailDTO> closeTicket(
            @Parameter(description = "ID of the ticket to close") @PathVariable("id") Long ticketId){
        var ticket = ticketService.closeTicket(ticketId);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "List filtered tickets", description = "Returns a paginated list of tickets, which can be filtered by various parameters.")
    @ApiResponse(responseCode = "200", description = "Ticket list retrieved successfully")
    @GetMapping 
    public ResponseEntity<Page<TicketListDTO>> getAllTickets(
        @Parameter(description = "Filter by customer ID")
        @RequestParam (required = false) Long customerId,
        
        @Parameter(description = "Filter by assigned technician ID")
        @RequestParam (required = false) Long technicianId,
        
        @Parameter(description = "Filter by ticket title (partial match)")
        @RequestParam (required = false) String title,
        
        @Parameter(description = "Filter by ticket status", schema = @Schema(allowableValues = {"OPEN", "IN_PROGRESS", "WAITING", "RESOLVED", "CLOSED"}))
        @RequestParam (required = false) String status,
        
        @Parameter(description = "Filter by ticket category", schema = @Schema(allowableValues = {"HARDWARE", "SOFTWARE", "NETWORK"}))
        @RequestParam (required = false) String category,
        
        @Parameter(description = "Filter by ticket priority", schema = @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}))
        @RequestParam (required = false) String priority,
        
        @Parameter(description = "Pagination and sorting parameters")
        @PageableDefault(page=0, size = 10, sort = "createdAt") Pageable pageable){
        
        var tickets = ticketService.getAllTickets(customerId, technicianId, title, status, category, priority, pageable);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get ticket by ID", description = "Returns the details of a specific ticket by its ID.")
    @ApiResponse(responseCode = "200", description = "Ticket found successfully")
    @ApiResponse(responseCode = "404", description = "Ticket not found")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDTO> getTicketById(
            @Parameter(description = "ID of the ticket to retrieve") @PathVariable Long id){
        var ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @Operation(summary = "Get dashboard metrics", description = "Returns ticket count metrics. The return varies depending on the role of the logged-in user (ADMIN sees everything, others only see their own tickets).")
    @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics(
            @Parameter(hidden = true) Authentication auth){
        var metricsDTO = ticketService.getDashboardMetrics(auth);
        return ResponseEntity.ok(metricsDTO);
    }

    @Operation(summary = "Delete a ticket", description = "Removes a ticket from the system (or marks it as inactive). Only CLIENT or ADMIN roles can perform this operation.")
    @ApiResponse(responseCode = "204", description = "Ticket deleted successfully")
    @PreAuthorize (value = "hasAnyRole('CLIENT', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(
            @Parameter(description = "ID of the ticket to delete") @PathVariable("id") Long ticketId){
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }
}
