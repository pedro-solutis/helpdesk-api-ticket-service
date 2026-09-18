package br.com.solutis.helpdesk.api_ticket_service.validation.user;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.solutis.helpdesk.api_ticket_service.client.UserClient;

public abstract class TicketUserValidator {

    @Autowired
    protected  UserClient userClient;

    public abstract boolean userExist(Long userId);
    
}