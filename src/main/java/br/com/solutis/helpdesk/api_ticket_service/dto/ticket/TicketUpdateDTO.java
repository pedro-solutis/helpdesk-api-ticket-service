package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import br.com.solutis.helpdesk.api_ticket_service.model.Category;
import br.com.solutis.helpdesk.api_ticket_service.model.Priority;
import br.com.solutis.helpdesk.api_ticket_service.model.Status;

public record TicketUpdateDTO(Priority priority, Category category, String description, Status status) {

}
