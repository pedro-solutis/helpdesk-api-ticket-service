package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
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
