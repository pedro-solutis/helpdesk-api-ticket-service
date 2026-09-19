package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AssignTechnicianDTO(
    @NotNull(message = "The technician ID is required.")
    @Positive(message = "The techincian ID must be greater than zero")
    Long technicianId
) {

}
