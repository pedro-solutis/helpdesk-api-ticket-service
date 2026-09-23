package br.com.solutis.helpdesk.api_ticket_service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

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

    @Query (
        value = 
        "select " +
            "count(*) as total_de_tickets, "+
            "count(*) filter (where t.status = 'OPEN') as total_em_aberto, "+
            "count(*) filter (where t.status IN ('IN_PROGRESS', 'WAITING')) as total_em_andamento, "+
            "count(*) filter (where t.status IN ('RESOLVED', 'CLOSED')) as total_resolvidos, "+
            "count(*) filter (where t.priority = 'CRITICAL') as total_critico "+
        "from tickets t "+
        "where :user_id IS NULL "+
        "or t.customer_id = :user_id "+
        "or t.technician_id = :user_id",
        nativeQuery = true
    )
    Object[] getTicketMetrics(@Param("user_id") Long userId);

}
