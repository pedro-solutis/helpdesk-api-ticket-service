package br.com.solutis.helpdesk.api_ticket_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.solutis.helpdesk.api_ticket_service.dto.user.UserClientDTO;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL:http://localhost:8081}", configuration = FeignClientConfiguration.class)
public interface UserClient {

    @GetMapping("/users/{id}")
    ResponseEntity<UserClientDTO> validateUserExists(@PathVariable("id") Long userId);
}

