package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

import jakarta.validation.constraints.NotNull;

public record AssignTechnicianDTO(
    @NotNull
    Long technicianId
) {

}
