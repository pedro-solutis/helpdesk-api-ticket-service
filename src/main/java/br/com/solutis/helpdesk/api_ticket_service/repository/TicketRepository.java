package br.com.solutis.helpdesk.api_ticket_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

    Ticket findByCustomerId(Long customerId);

}
