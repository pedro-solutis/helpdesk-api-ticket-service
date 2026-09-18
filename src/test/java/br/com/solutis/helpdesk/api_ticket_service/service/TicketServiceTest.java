package br.com.solutis.helpdesk.api_ticket_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketUpdateDTO;
import br.com.solutis.helpdesk.api_ticket_service.infra.amqp.TicketEventProducer;
import br.com.solutis.helpdesk.api_ticket_service.infra.exception.ResourceNotFoundException;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.repository.TicketRepository;
import br.com.solutis.helpdesk.api_ticket_service.validation.user.CustomerValidator;
import br.com.solutis.helpdesk.api_ticket_service.validation.user.TechnicianValidator;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CustomerValidator customerValidator;

    @Mock
    private TechnicianValidator technicianValidator;

    @Mock
    private TicketEventProducer ticketEventProducer;

    private Ticket createMockTicket() {
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Issue", "Desc", "NETWORK", "HIGH", 10L);
        Ticket ticket = new Ticket(dto);
        return ticket;
    }

    @Test
    @DisplayName("Should create ticket successfully and dispatch event")
    public void testCreateTicket_Success(){
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Issue", "Desc", "NETWORK", "HIGH", 10L);
        
        when(customerValidator.userExist(10L)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        TicketDetailDTO result = ticketService.createTicket(dto);

        assertNotNull(result);
        assertEquals("Issue", result.title());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(ticketEventProducer, times(1)).ticketCreatedEvent(any(Ticket.class));
    }

    @Test
    @DisplayName("Should throw exception when customer does not exist")
    public void testCreateTicket_WhenCustomerInvalid_ShouldThrowException(){
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Issue", "Desc", "NETWORK", "HIGH", 10L);
        when(customerValidator.userExist(10L)).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.createTicket(dto);
        });

        assertEquals("This user can not be assigned as a customer", exception.getMessage());
        verify(ticketRepository, never()).save(any());
        verify(ticketEventProducer, never()).ticketCreatedEvent(any());
    }

    @Test
    @DisplayName("Should update ticket successfully")
    public void testUpdateTicket_Success(){
        Ticket mockTicket = createMockTicket();
        TicketUpdateDTO updateDto = new TicketUpdateDTO("LOW", "SOFTWARE", "New Desc", "IN_PROGRESS");
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        TicketDetailDTO result = ticketService.updateTicket(1L, updateDto);

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.status());
        verify(ticketRepository).save(mockTicket);
        verify(ticketEventProducer).ticketStatusChangedEvent(any(Ticket.class), any());
    }

    @Test
    @DisplayName("Should throw exception when updating a closed ticket")
    public void testUpdateTicket_WhenClosed_ShouldThrowException(){
        Ticket mockTicket = createMockTicket();
        mockTicket.closeTicket();
        TicketUpdateDTO updateDto = new TicketUpdateDTO("LOW", "SOFTWARE", "New Desc", "IN_PROGRESS");
        
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketService.updateTicket(1L, updateDto);
        });

        assertEquals("It is not possible change status for a closed ticket.", exception.getMessage());
    }

    @Test
    @DisplayName("Should assign technician successfully")
    public void testAssignTechnician_Success(){
        Ticket mockTicket = createMockTicket();
        AssignTechnicianDTO assignDto = new AssignTechnicianDTO(20L);

        when(technicianValidator.userExist(20L)).thenReturn(true);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        TicketDetailDTO result = ticketService.assignTechnician(1L, assignDto);

        assertNotNull(result);
        assertEquals(Status.IN_PROGRESS, result.status());
        assertEquals(20L, mockTicket.getTechnicianId());
        verify(ticketEventProducer).ticketAssignedEvent(mockTicket);
    }

    @Test
    @DisplayName("Should close ticket and trigger event")
    public void testCloseTicket(){
        Ticket mockTicket = createMockTicket();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        TicketDetailDTO result = ticketService.closeTicket(1L);

        assertEquals(Status.CLOSED, result.status());
        verify(ticketEventProducer).ticketStatusChangedEvent(eq(mockTicket), eq(Status.OPEN));
    }

    @Test
    public void testGetAllTicketByCustomerId(){
        Page<Ticket> page = new PageImpl<>(List.of(createMockTicket()));
        when(ticketRepository.findAllByCustomerId(eq(10L), any(Pageable.class))).thenReturn(page);

        Page<TicketListDTO> result = ticketService.getAllTicketByCustomerId(10L, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testGetAllTickets(){
        Page<Ticket> page = new PageImpl<>(List.of(createMockTicket()));
        when(ticketRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<TicketListDTO> result = ticketService.getAllTickets(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testGetTicketById(){
        Ticket mockTicket = createMockTicket();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));

        TicketDetailDTO result = ticketService.getTicketById(1L);

        assertNotNull(result);
    }

    @Test
    public void testGetTicketById_NotFound(){
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketById(1L));
    }

    @Test
    public void testSearchTicketByTitle(){
        Page<Ticket> page = new PageImpl<>(List.of(createMockTicket()));
        when(ticketRepository.findByTitleContainingIgnoreCase(eq("Issue"), any(Pageable.class))).thenReturn(page);

        Page<TicketListDTO> result = ticketService.searchTicketByTitle("Issue", PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }
    
    @Test
    public void testFilterTickets(){
        Page<Ticket> page = new PageImpl<>(List.of(createMockTicket()));
        when(ticketRepository.filterTickets(any(), any(), any(), any())).thenReturn(page);

        Page<TicketListDTO> result = ticketService.filterTickets(Status.OPEN, null, null, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    public void testDeleteTicket() {
        Ticket mockTicket = createMockTicket();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));

        ticketService.deleteTicket(1L);

        assertFalse(mockTicket.isActive());
        assertEquals(Status.CLOSED, mockTicket.getStatus());
        verify(ticketRepository).save(mockTicket);
        verify(ticketEventProducer).ticketStatusChangedEvent(eq(mockTicket), eq(Status.OPEN));
    }
}
