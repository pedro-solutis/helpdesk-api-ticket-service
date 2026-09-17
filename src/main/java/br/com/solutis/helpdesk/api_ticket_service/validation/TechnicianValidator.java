package br.com.solutis.helpdesk.api_ticket_service.validation;

import org.springframework.stereotype.Component;

import org.springframework.http.ResponseEntity;
import br.com.solutis.helpdesk.api_ticket_service.dto.user.UserClientDTO;
import feign.FeignException;

@Component 
public class TechnicianValidator extends TicketUserValidator {

    @Override
    public boolean userExist(Long userId) {
        try {
            ResponseEntity<UserClientDTO> response = userClient.validateUserExists(userId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return "TECHNICIAN".equalsIgnoreCase(response.getBody().role());
            }
            return false;
        } catch (FeignException.NotFound e) {
            return false;
        }
    }

}
