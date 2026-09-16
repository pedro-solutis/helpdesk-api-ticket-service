package br.com.solutis.helpdesk.api_ticket_service.model;

import jakarta.validation.constraints.NotNull;

public record AssignTechnicianDTO(
    @NotNull
    Long technicianId
) {

}
