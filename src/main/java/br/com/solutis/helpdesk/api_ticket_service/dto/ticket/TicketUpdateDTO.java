package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;
import br.com.solutis.helpdesk.api_ticket_service.validation.enums.ValueOfEnum;
import jakarta.validation.constraints.Size;

public record TicketUpdateDTO(
    
    @ValueOfEnum (enumClass = Priority.class, message = "Invalid priority. AcceptedValue: LOW, MEDIUM, HIGH, CRITICAL.")
    String priority,
    
    @ValueOfEnum (enumClass = Category.class, message = "Invalid category. AcceptedValue: SOFTWARE, HARDWARE, NETWORD.")
    String category,
    
    @Size(min = 3)
    String description,

    @ValueOfEnum (enumClass = Status.class, message = "Invalid status. AcceptedValue: IN_PROGRESS, WAITING, RESOLVED.")
    String status) {

}
