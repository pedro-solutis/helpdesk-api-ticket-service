package br.com.solutis.helpdesk.api_ticket_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "tickets")
@Getter 
@Setter
@AllArgsConstructor 
@NoArgsConstructor
public class Ticket {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank 
    private String title;
    
    @NotBlank 
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull 
    private Priority priority;
    
    @Enumerated(EnumType.STRING)
    @NotNull  
    private Status status = Status.OPEN;
    
    @Enumerated(EnumType.STRING)
    @NotNull 
    private Category category;
    
    @NotNull 
    private Long customerId;
    
    private Long technicianId;
    
    @NotNull 
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @NotNull 
    private LocalDateTime updatedAt;

    public Ticket(TicketRegistrationDTO ticketRegistrationDTO) {
        title = ticketRegistrationDTO.title();
        description = ticketRegistrationDTO.description();
        priority = ticketRegistrationDTO.priority();
        category = ticketRegistrationDTO.category();
        customerId = ticketRegistrationDTO.customerId();
        updatedAt = LocalDateTime.now();
    }

    public void closeTicket(){
        status = Status.CLOSED;
    }

}
