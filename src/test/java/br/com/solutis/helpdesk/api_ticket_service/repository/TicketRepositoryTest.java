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

    private Ticket createTicketAndPersist(String title, Category category, Priority priority, Long customerId) {
        TicketRegistrationDTO dto = new TicketRegistrationDTO(title, "Test Description", category.name(), priority.name(), customerId);
        Ticket ticket = new Ticket(dto);
        return ticketRepository.save(ticket);
    }

    @Test
    @DisplayName("Should find tickets by exact customer ID")
    void testFindAllByCustomerId() {
        createTicketAndPersist("Ticket 1", Category.SOFTWARE, Priority.LOW, 10L);
        createTicketAndPersist("Ticket 2", Category.HARDWARE, Priority.HIGH, 20L);

        Page<Ticket> page = ticketRepository.findAllByCustomerId(10L, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getCustomerId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should find tickets containing title ignoring case")
    void testFindByTitleContainingIgnoreCase() {
        createTicketAndPersist("Network is down", Category.NETWORK, Priority.HIGH, 10L);
        createTicketAndPersist("Keyboard broken", Category.HARDWARE, Priority.LOW, 10L);

        Page<Ticket> page = ticketRepository.findByTitleContainingIgnoreCase("nEtWorK", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Network is down");
    }

    @Test
    @DisplayName("Should filter tickets based on optional parameters")
    void testFilterTickets() {
        createTicketAndPersist("Ticket A", Category.SOFTWARE, Priority.HIGH, 10L);
        createTicketAndPersist("Ticket B", Category.NETWORK, Priority.HIGH, 10L);
        createTicketAndPersist("Ticket C", Category.SOFTWARE, Priority.LOW, 10L);

        Page<Ticket> onlyCategory = ticketRepository.filterTickets(null, Category.SOFTWARE, null, PageRequest.of(0, 10));
        assertThat(onlyCategory.getContent()).hasSize(2);

        Page<Ticket> categoryAndPriority = ticketRepository.filterTickets(null, Category.SOFTWARE, Priority.HIGH, PageRequest.of(0, 10));
        assertThat(categoryAndPriority.getContent()).hasSize(1);
        assertThat(categoryAndPriority.getContent().get(0).getTitle()).isEqualTo("Ticket A");
    }
}
