package br.com.solutis.helpdesk.api_ticket_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.DashboardMetricsDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.AssignTechnicianDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketDetailDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketListDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketRegistrationDTO;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketUpdateDTO;
import br.com.solutis.helpdesk.api_ticket_service.infra.amqp.TicketEventProducer;
import br.com.solutis.helpdesk.api_ticket_service.infra.exception.ResourceNotFoundException;
import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.repository.TicketRepository;
import br.com.solutis.helpdesk.api_ticket_service.validation.user.CustomerValidator;
import br.com.solutis.helpdesk.api_ticket_service.validation.user.TechnicianValidator;

@Service 
public class TicketService {

    @Autowired 
    private TicketRepository ticketRepository;

    @Autowired 
    private CustomerValidator customerValidator;

    @Autowired 
    private TechnicianValidator technicianValidator;

    @Autowired
    private TicketEventProducer ticketEventProducer;

    public TicketDetailDTO createTicket(TicketRegistrationDTO ticketRegistrationDTO){
        if(!customerValidator.userExist(ticketRegistrationDTO.customerId()))
            throw new IllegalArgumentException("This user can not be assigned as a customer");
        var newTicket = new Ticket(ticketRegistrationDTO);
        ticketRepository.save(newTicket);
        ticketEventProducer.ticketCreatedEvent(newTicket);
        return new TicketDetailDTO(newTicket);
    }

    public TicketDetailDTO updateTicket(Long id,TicketUpdateDTO ticketDto){
        var toUpdateTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if(ticketDto.status() != null && toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible change status for a closed ticket.");
        var lastStatus = toUpdateTicket.getStatus();
        toUpdateTicket.updateTicket(ticketDto);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        if (ticketDto.status() != null)
            ticketEventProducer.ticketStatusChangedEvent(updatedTicket, lastStatus);
        ticketEventProducer.ticketUpdatedEvent(updatedTicket);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO assignTechnician(Long ticketId, AssignTechnicianDTO assignTechnicianDTO){
        if (!technicianValidator.userExist(assignTechnicianDTO.technicianId()))
            throw new IllegalArgumentException("This user can not be assigned as a technician");
        var toUpdateTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (toUpdateTicket.isClose())
            throw new IllegalArgumentException("It is not possible assign a technician for a closed ticket.");
        toUpdateTicket.assignTechnician(assignTechnicianDTO);
        var updatedTicket = ticketRepository.save(toUpdateTicket);
        ticketEventProducer.ticketAssignedEvent(updatedTicket);
        ticketEventProducer.ticketStatusChangedEvent(updatedTicket, Status.OPEN);
        return new TicketDetailDTO(updatedTicket);
    }

    public TicketDetailDTO closeTicket(Long id){
        var toCloseTicket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        if (toCloseTicket.getStatus() != Status.RESOLVED)
            throw new IllegalArgumentException("Its not possible close a ticket that is not resolved");
        var lastStatus = toCloseTicket.getStatus();
        if (toCloseTicket.getTechnicianId() == null )
            throw new IllegalArgumentException("Its not possible close a ticket with no technician assigned");
        toCloseTicket.closeTicket();
        var closedTicket = ticketRepository.save(toCloseTicket);
        ticketEventProducer.ticketStatusChangedEvent(closedTicket, lastStatus);
        return new TicketDetailDTO(closedTicket);
    }

    public Page<TicketListDTO> getAllTickets(
        Long customerId,
        Long technicianId,
        String title,
        String status,
        String category,
        String priority,
        Pageable pageable){
        var tickets = ticketRepository.filterTickets(
            customerId,
            technicianId,
            title != null ? title.toUpperCase() : null, 
            status != null ? Status.valueOf(status.toUpperCase()) : null,
            category != null ? Category.valueOf(category.toUpperCase()) : null,
            priority != null ? Priority.valueOf(priority.toUpperCase()) : null,
            pageable);
        return tickets.map(TicketListDTO::new);
    }

    public TicketDetailDTO getTicketById(Long id){
        var ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return new TicketDetailDTO(ticket);
    }

    public DashboardMetricsDTO getDashboardMetrics(Authentication auth) {
        long total = 0L, open = 0L, in_progress = 0L, resolved = 0L, critical = 0L;
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));
        Long userId = isAdmin ? null : (Long) auth.getPrincipal();
        Object[] metrics = ticketRepository.getTicketMetrics(userId);
        if (metrics != null && metrics.length > 0){
            Object[] row = (metrics[0] instanceof Object[]) ? (Object[]) metrics[0] : metrics;
            total = ((Number) row[0]).longValue();
            open = ((Number) row[1]).longValue();
            in_progress = ((Number) row[2]).longValue();
            resolved = ((Number) row[3]).longValue();
            critical = ((Number) row[4]).longValue();
        }
        return new DashboardMetricsDTO(total, open, in_progress, resolved, critical);
    }

    public void deleteTicket(Long ticketId) {
        var toDeleteTicket = ticketRepository.findById(ticketId).orElseThrow(() -> new ResourceNotFoundException("Ticket not founded"));
        toDeleteTicket.deleteTicket();
        toDeleteTicket.closeTicket();
        ticketRepository.save(toDeleteTicket);
        ticketEventProducer.ticketDeletedEvent(toDeleteTicket);
    }

}
