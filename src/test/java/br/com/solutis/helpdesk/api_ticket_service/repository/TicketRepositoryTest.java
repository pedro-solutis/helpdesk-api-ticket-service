package br.com.solutis.helpdesk.api_ticket_service.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Ticket;
import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.TicketRegistrationDTO;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @MockitoBean
    private RabbitAdmin rabbitAdmin;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
    }

    private Ticket createTicketAndPersist(String title, Category category, Priority priority, Long customerId) {
        TicketRegistrationDTO dto = new TicketRegistrationDTO(title, "Test Description", category.name(), priority.name(), customerId);
        Ticket ticket = new Ticket(dto);
        return ticketRepository.save(ticket);
    }

    @Test
    @DisplayName("Should filter tickets based on optional parameters")
    void testFilterTickets() {
        createTicketAndPersist("Ticket A", Category.SOFTWARE, Priority.HIGH, 10L);
        createTicketAndPersist("Ticket B", Category.NETWORK, Priority.HIGH, 10L);
        createTicketAndPersist("Ticket C", Category.SOFTWARE, Priority.LOW, 10L);

        Page<Ticket> onlyCategory = ticketRepository.filterTickets(null, null, null, null, Category.SOFTWARE, null, PageRequest.of(0, 10));
        assertThat(onlyCategory.getContent()).hasSize(2);

        Page<Ticket> categoryAndPriority = ticketRepository.filterTickets(null, null, null, null, Category.SOFTWARE, Priority.HIGH, PageRequest.of(0, 10));
        assertThat(categoryAndPriority.getContent()).hasSize(1);
        assertThat(categoryAndPriority.getContent().get(0).getTitle()).isEqualTo("Ticket A");
    }

    @Test
    @DisplayName("Should return correct metrics for tickets")
    void testGetTicketMetrics() {
        ticketRepository.deleteAll();

        createTicketAndPersist("Ticket A", Category.SOFTWARE, Priority.CRITICAL, 10L); 
        Ticket t2 = createTicketAndPersist("Ticket B", Category.NETWORK, Priority.HIGH, 10L);
        t2.assignTechnician(new br.com.solutis.helpdesk.api_ticket_service.dto.ticket.AssignTechnicianDTO(20L)); 
        ticketRepository.save(t2);

        Object[] metrics = ticketRepository.getTicketMetrics(null);
        assertThat(metrics).isNotNull();
        
        Object[] row = (metrics[0] instanceof Object[]) ? (Object[]) metrics[0] : metrics;
        
        assertThat(((Number) row[0]).longValue()).isEqualTo(2L);
        assertThat(((Number) row[1]).longValue()).isEqualTo(1L);
        assertThat(((Number) row[2]).longValue()).isEqualTo(1L);
        assertThat(((Number) row[3]).longValue()).isEqualTo(0L);
        assertThat(((Number) row[4]).longValue()).isEqualTo(1L);
    }
}
