package br.com.solutis.helpdesk.api_ticket_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    
    @Column(nullable = false) 
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT") 
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) 
    private Priority priority;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)  
    private Status status = Status.OPEN;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) 
    private Category category;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "technician_id", nullable = true)
    private Long technicianId;
    
    @Column(name = "created_at")
    @CreationTimestamp  
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp 
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
