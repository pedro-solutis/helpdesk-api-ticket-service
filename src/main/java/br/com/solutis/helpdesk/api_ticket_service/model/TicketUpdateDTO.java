package br.com.solutis.helpdesk.api_ticket_service.model;

public record TicketUpdateDTO(Priority priority, Category category, String description, Status status) {

}
