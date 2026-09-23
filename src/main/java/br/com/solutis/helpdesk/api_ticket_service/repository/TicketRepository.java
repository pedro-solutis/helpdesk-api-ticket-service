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
        "(:customerId IS NULL OR t.customerId = :customerId) AND " +
        "(:technicianId IS NULL OR t.technicianId = :technicianId) AND " +
        "(:title IS NULL OR UPPER(t.title) LIKE %:title%) AND " +
        "(:status IS NULL OR t.status = :status) AND " +
        "(:category IS NULL OR t.category = :category) AND " +
        "(:priority IS NULL OR t.priority = :priority)"
    )
    Page<Ticket> filterTickets(
        Long customerId,
        Long technicianId,
        String title,
        Status status,
        Category category,
        Priority priority,
        Pageable pageable);

    long countByStatusEquals(Status open);

    long countByTechnicianId(Long userId);

    long countByTechnicianIdAndStatusEquals(Long userId, Status resolved);

    long countByCustomerId(Long userId);

    long countByCustomerIdAndStatusEquals(Long userId, Status open);

    long countByPriorityEquals(Priority priority);

    long countByTechnicianIdAndPriorityEquals(Long userId, Priority priority);

    long countByCustomerIdAndPriorityEquals(Long userId, Priority priority);

}
