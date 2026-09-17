package br.com.solutis.helpdesk.api_ticket_service.infra.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

