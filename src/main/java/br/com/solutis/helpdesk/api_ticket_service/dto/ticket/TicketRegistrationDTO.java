package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.validation.enums.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TicketRegistrationDTO(

    @NotBlank(message = "The ticket title is required.") 
    String title,
    
    @NotBlank(message = "The ticket description is required.")
    String description,
    
    @NotBlank(message = "The category is required.")
    @ValueOfEnum (enumClass = Category.class, message = "Invalid category. AcceptedValue: SOFTWARE, HARDWARE and NETWORD.")
    String category,
    
    @NotBlank(message = "The priority is required")
    @ValueOfEnum (enumClass = Priority.class, message = "Invalid priority. AcceptedValue: LOW, MEDIUM, HIGH, CRITICAL.")
    String priority,
    
    @NotNull(message = "The customer ID is required.")
    @Positive(message = "The customer ID must be greater than zero") 
    Long customerId
) {

}
