package br.com.solutis.helpdesk.api_ticket_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.solutis.helpdesk.api_ticket_service.dto.ticket.*;
import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.service.TicketService;

@SpringBootTest
public class TicketControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private TicketService ticketService;

    @MockitoBean
    private RabbitAdmin rabbitAdmin;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private TicketDetailDTO createMockTicketDetail() {
        return new TicketDetailDTO(1L, "Issue with VPN", "Cannot connect to VPN", Status.OPEN, Category.NETWORK, Priority.HIGH, 10L, null, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 201 Created when creating a valid ticket")
    void testCreateTicket() throws Exception {
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Issue with VPN", "Cannot connect to VPN", "NETWORK", "HIGH", 10L);
        TicketDetailDTO detailDto = createMockTicketDetail();

        when(ticketService.createTicket(any())).thenReturn(detailDto);

        mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Issue with VPN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 403 Forbidden when creating a ticket with invalid role")
    void testCreateTicketForbidden() throws Exception {
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Issue with VPN", "Cannot connect to VPN", "NETWORK", "HIGH", 10L);

        mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    @DisplayName("Should return 200 OK when updating a ticket")
    void testUpdateTicket() throws Exception {
        TicketUpdateDTO dto = new TicketUpdateDTO("HIGH", "NETWORK", "Updated description", "IN_PROGRESS");
        TicketDetailDTO detailDto = createMockTicketDetail();

        when(ticketService.updateTicket(eq(1L), any())).thenReturn(detailDto);

        mockMvc.perform(put("/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 403 Forbidden when updating a ticket with invalid role")
    void testUpdateTicketForbidden() throws Exception {
        TicketUpdateDTO dto = new TicketUpdateDTO("HIGH", "NETWORK", "Updated description", "IN_PROGRESS");

        mockMvc.perform(put("/tickets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 OK when assigning technician")
    void testAssignTechnician() throws Exception {
        AssignTechnicianDTO dto = new AssignTechnicianDTO(20L);
        TicketDetailDTO detailDto = createMockTicketDetail();

        when(ticketService.assignTechnician(eq(1L), any())).thenReturn(detailDto);

        mockMvc.perform(patch("/tickets/technician/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when assigning technician with invalid role")
    void testAssignTechnicianForbidden() throws Exception {
        AssignTechnicianDTO dto = new AssignTechnicianDTO(20L);

        mockMvc.perform(patch("/tickets/technician/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    @DisplayName("Should return 200 OK when closing ticket")
    void testCloseTicket() throws Exception {
        TicketDetailDTO detailDto = createMockTicketDetail();
        when(ticketService.closeTicket(1L)).thenReturn(detailDto);

        mockMvc.perform(patch("/tickets/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when closing ticket with invalid role")
    void testCloseTicketForbidden() throws Exception {
        mockMvc.perform(patch("/tickets/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 200 OK and list tickets by customer")
    void testSearchTicketByCustomerId() throws Exception {
        when(ticketService.getAllTicketByCustomerId(eq(10L), any())).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/tickets/customer/10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    @DisplayName("Should return 403 Forbidden when listing tickets by customer with invalid role")
    void testSearchTicketByCustomerIdForbidden() throws Exception {
        mockMvc.perform(get("/tickets/customer/10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 OK and all tickets")
    void testGetAllTickets() throws Exception {
        when(ticketService.getAllTickets(any())).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 403 Forbidden when getting all tickets with invalid role")
    void testGetAllTicketsForbidden() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 200 OK when finding by ID")
    void testGetTicketById() throws Exception {
        TicketDetailDTO detailDto = createMockTicketDetail();
        when(ticketService.getTicketById(1L)).thenReturn(detailDto);

        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "TECHNICIAN")
    @DisplayName("Should return 200 OK when searching by title")
    void testSearchTicketByTitle() throws Exception {
        when(ticketService.searchTicketByTitle(eq("VPN"), any())).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/tickets/search").param("title", "VPN"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 200 OK when filtering tickets")
    void testFilterTickets() throws Exception {
        when(ticketService.filterTickets(eq(Status.OPEN), eq(Category.NETWORK), eq(Priority.HIGH), any()))
                .thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/tickets/filter")
                .param("status", "OPEN")
                .param("category", "NETWORK")
                .param("priority", "HIGH"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 204 No Content when deleting ticket")
    void testDeleteTicket() throws Exception {
        mockMvc.perform(delete("/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 400 Bad Request when creating ticket with invalid data")
    void testCreateTicket_WithInvalidData_ShouldReturn400() throws Exception {
        TicketRegistrationDTO invalidDto = new TicketRegistrationDTO("", "Description", "NETWORK", "HIGH", null);

        mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 400 Bad Request when customer does not exist or invalid role")
    void testCreateTicket_WhenCustomerInvalid_ShouldReturn400() throws Exception {
        TicketRegistrationDTO dto = new TicketRegistrationDTO("Title", "Description", "NETWORK", "HIGH", 10L);

        when(ticketService.createTicket(any())).thenThrow(new IllegalArgumentException("This user can not be assigned as a customer"));

        mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("This user can not be assigned as a customer"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("Should return 404 Not Found when ticket ID does not exist")
    void testGetTicketById_WhenTicketDoesNotExist_ShouldReturn404() throws Exception {
        when(ticketService.getTicketById(99L)).thenThrow(new br.com.solutis.helpdesk.api_ticket_service.infra.exception.ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(get("/tickets/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Ticket not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 Bad Request when assigning technician to a closed ticket")
    void testAssignTechnician_WhenTicketIsClosed_ShouldReturn400() throws Exception {
        AssignTechnicianDTO dto = new AssignTechnicianDTO(20L);

        when(ticketService.assignTechnician(eq(1L), any())).thenThrow(new IllegalArgumentException("It is not possible assign a technician for a closed ticket."));

        mockMvc.perform(patch("/tickets/technician/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("It is not possible assign a technician for a closed ticket."));
    }
}
