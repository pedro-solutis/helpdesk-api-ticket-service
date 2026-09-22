package br.com.solutis.helpdesk.api_ticket_service.dto.ticket;

public record DashboardMetricsDTO(Long total, Long open, Long in_progess, Long resolved, Long critical) {
}
