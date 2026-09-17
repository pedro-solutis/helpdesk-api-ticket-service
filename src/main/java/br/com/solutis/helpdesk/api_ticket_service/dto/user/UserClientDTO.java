package br.com.solutis.helpdesk.api_ticket_service.dto.user;

import java.time.LocalDateTime;

public record UserClientDTO(
    Long id,
    String name,
    String email,
    String role,
    boolean active,
    LocalDateTime createdAt) {

}
