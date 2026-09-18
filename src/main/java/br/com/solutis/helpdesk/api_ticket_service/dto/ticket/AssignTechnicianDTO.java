package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import jakarta.validation.constraints.NotNull;

public record AssignTechnicianDTO(
    @NotNull(message = "The technician ID is required.")
    Long technicianId
) {

}
