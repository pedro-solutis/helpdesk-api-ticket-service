package br.com.solutis.helpdesk.api_ticket_service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketRegistrationDTO(
    @NotBlank 
    String title,
    
    @NotBlank 
    String description,
    
    @NotNull 
    Category category,
    
    @NotNull 
    Priority priority,
    
    @NotNull 
    Long customerId
) {

}
