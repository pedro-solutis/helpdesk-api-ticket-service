package br.com.solutis.helpdesk.api_ticket_service.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketUpdateDTO;

public class TicketTest {

    @Test
    @DisplayName("Should initialize Ticket correctly from TicketRegistrationDTO")
    void testConstructorFromDTO() {
        TicketRegistrationDTO dto = new TicketRegistrationDTO("No Internet", "Router is blinking red", "NETWORK", "HIGH", 10L);
        
        Ticket ticket = new Ticket(dto);
        
        assertEquals("No Internet", ticket.getTitle());
        assertEquals("Router is blinking red", ticket.getDescription());
        assertEquals(Category.NETWORK, ticket.getCategory());
        assertEquals(Priority.HIGH, ticket.getPriority());
        assertEquals(10L, ticket.getCustomerId());
        
        // Defaults
        assertEquals(Status.OPEN, ticket.getStatus());
        assertTrue(ticket.isActive());
        assertNull(ticket.getTechnicianId());
    }

    @Test
    @DisplayName("Should assign technician and change status to IN_PROGRESS")
    void testAssignTechnician() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        AssignTechnicianDTO dto = new AssignTechnicianDTO(42L);
        
        ticket.assignTechnician(dto);
        
        assertEquals(42L, ticket.getTechnicianId());
        assertEquals(Status.IN_PROGRESS, ticket.getStatus());
    }

    @Test
    @DisplayName("Should change ticket status to CLOSED")
    void testCloseTicket() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        
        ticket.closeTicket();
        
        assertEquals(Status.CLOSED, ticket.getStatus());
    }

    @Test
    @DisplayName("Should return true when ticket is closed and false otherwise")
    void testIsClose() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        
        // Starts as OPEN
        assertFalse(ticket.isClose());
        
        // Assign technician changes to IN_PROGRESS
        ticket.assignTechnician(new AssignTechnicianDTO(42L));
        assertFalse(ticket.isClose());
        
        // Close ticket
        ticket.closeTicket();
        assertTrue(ticket.isClose());
    }

    @Test
    @DisplayName("Should partially update ticket fields when values are provided")
    void testUpdateTicket_WithAllFields() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        TicketUpdateDTO updateDto = new TicketUpdateDTO("HIGH", "HARDWARE", "New description", "RESOLVED");
        
        ticket.updateTicket(updateDto);
        
        assertEquals(Priority.HIGH, ticket.getPriority());
        assertEquals(Category.HARDWARE, ticket.getCategory());
        assertEquals("New description", ticket.getDescription());
        assertEquals(Status.RESOLVED, ticket.getStatus());
    }

    @Test
    @DisplayName("Should not update ticket fields when values are null")
    void testUpdateTicket_WithNullFields() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        // Provide only Priority, others are null
        TicketUpdateDTO updateDto = new TicketUpdateDTO("HIGH", null, null, null);
        
        ticket.updateTicket(updateDto);
        
        // Updated field
        assertEquals(Priority.HIGH, ticket.getPriority());
        
        // Unchanged fields
        assertEquals(Category.SOFTWARE, ticket.getCategory());
        assertEquals("Desc", ticket.getDescription());
        assertEquals(Status.OPEN, ticket.getStatus());
    }

    @Test
    @DisplayName("Should logically delete ticket by setting active to false")
    void testDeleteTicket() {
        Ticket ticket = new Ticket(new TicketRegistrationDTO("Issue", "Desc", "SOFTWARE", "LOW", 10L));
        assertTrue(ticket.isActive());
        
        ticket.deleteTicket();
        
        assertFalse(ticket.isActive());
    }
}
