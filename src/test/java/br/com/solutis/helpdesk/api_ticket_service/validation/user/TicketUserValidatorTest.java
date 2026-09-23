package br.com.solutis.helpdesk.api_ticket_service.validation.user;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.com.solutis.helpdesk.api_ticket_service.client.UserClient;
import br.com.solutis.helpdesk.api_ticket_service.dto.user.UserClientDTO;
import feign.FeignException;

@ExtendWith(MockitoExtension.class)
public class TicketUserValidatorTest {

    @Mock
    private UserClient userClient;

    @InjectMocks
    private CustomerValidator customerValidator;

    @InjectMocks
    private TechnicianValidator technicianValidator;

    private UserClientDTO createUserDto(String role) {
        return new UserClientDTO(1L, "User Name", "user@test.com", role, true, java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("CustomerValidator should return true when user is CLIENT")
    void testCustomerValidator_UserIsCustomer_ReturnsTrue() {
        when(userClient.validateUserExists(1L))
                .thenReturn(ResponseEntity.ok(createUserDto("CLIENT")));

        boolean result = customerValidator.userExist(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("CustomerValidator should return true when user is client (case insensitive)")
    void testCustomerValidator_UserIsCustomerLowercase_ReturnsTrue() {
        when(userClient.validateUserExists(1L))
                .thenReturn(ResponseEntity.ok(createUserDto("client")));

        boolean result = customerValidator.userExist(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("CustomerValidator should return false when user is TECHNICIAN")
    void testCustomerValidator_UserIsTechnician_ReturnsFalse() {
        when(userClient.validateUserExists(1L))
                .thenReturn(ResponseEntity.ok(createUserDto("TECHNICIAN")));

        boolean result = customerValidator.userExist(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("CustomerValidator should return false when Feign throws NotFound exception")
    void testCustomerValidator_UserNotFound_ReturnsFalse() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(userClient.validateUserExists(99L)).thenThrow(notFoundException);

        boolean result = customerValidator.userExist(99L);

        assertFalse(result);
    }

    @Test
    @DisplayName("CustomerValidator should return false when response body is null")
    void testCustomerValidator_BodyIsNull_ReturnsFalse() {
        when(userClient.validateUserExists(1L))
                .thenReturn(ResponseEntity.ok(null));

        boolean result = customerValidator.userExist(1L);

        assertFalse(result);
    }

    @Test
    @DisplayName("TechnicianValidator should return true when user is TECHNICIAN")
    void testTechnicianValidator_UserIsTechnician_ReturnsTrue() {
        when(userClient.validateUserExists(2L))
                .thenReturn(ResponseEntity.ok(createUserDto("TECHNICIAN")));

        boolean result = technicianValidator.userExist(2L);

        assertTrue(result);
    }

    @Test
    @DisplayName("TechnicianValidator should return false when user is CUSTOMER")
    void testTechnicianValidator_UserIsCustomer_ReturnsFalse() {
        when(userClient.validateUserExists(2L))
                .thenReturn(ResponseEntity.ok(createUserDto("CUSTOMER")));

        boolean result = technicianValidator.userExist(2L);

        assertFalse(result);
    }

    @Test
    @DisplayName("TechnicianValidator should return false when Feign throws NotFound exception")
    void testTechnicianValidator_UserNotFound_ReturnsFalse() {
        FeignException.NotFound notFoundException = mock(FeignException.NotFound.class);
        when(userClient.validateUserExists(99L)).thenThrow(notFoundException);

        boolean result = technicianValidator.userExist(99L);

        assertFalse(result);
    }

    @Test
    @DisplayName("TechnicianValidator should return false when response body is null")
    void testTechnicianValidator_BodyIsNull_ReturnsFalse() {
        when(userClient.validateUserExists(2L))
                .thenReturn(ResponseEntity.ok(null));

        boolean result = technicianValidator.userExist(2L);

        assertFalse(result);
    }
}
