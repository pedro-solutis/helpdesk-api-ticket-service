package br.com.solutis.helpdesk.api_ticket_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

	Page<Ticket> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query(
        "SELECT t FROM Ticket t WHERE " + 
        "(:status IS NULL OR t.status = :status) AND " +
        "(:category IS NULL OR t.category = :category) AND " +
        "(:priority IS NULL OR t.priority = :priority)"
    )
    Page<Ticket> filterTickets(Status status, Category category, Priority priority, Pageable pageable);

    Page<Ticket> findAllByCustomerId(Long customerId, Pageable pageable);

    Page<Ticket> findAllByTechnicianId(Long technicianId, Pageable pageable);

    long countByStatusEquals(Status open);

    long countByTechnicianId(Long userId);

    long countByTechnicianIdAndStatusEquals(Long userId, Status resolved);

    long countByCustomerId(Long userId);

    long countByCustomerIdAndStatusEquals(Long userId, Status open);

    long countByPriorityEquals(Priority priority);

    long countByTechnicianIdAndPriorityEquals(Long userId, Priority priority);

    long countByCustomerIdAndPriorityEquals(Long userId, Priority priority);

}
