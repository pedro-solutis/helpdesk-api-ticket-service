package br.com.solutis.helpdesk.api_ticket_service.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class UserClientTest {

    @Test
    @DisplayName("Should have FeignClient annotation with correct name and url properties")
    void testFeignClientAnnotation() {
        // As it's an interface, we test its contract and annotations
        FeignClient feignClient = UserClient.class.getAnnotation(FeignClient.class);
        
        assertNotNull(feignClient, "UserClient should be annotated with @FeignClient");
        assertEquals("user-service", feignClient.name(), "FeignClient name should be 'user-service'");
        assertEquals("${user.service.url:http://localhost:8081}", feignClient.url(), "FeignClient url should use the property placeholder");
    }

    @Test
    @DisplayName("Should have GetMapping annotation on validateUserExists with correct endpoint")
    void testValidateUserExistsMapping() throws NoSuchMethodException {
        Method method = UserClient.class.getMethod("validateUserExists", Long.class);
        
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        
        assertNotNull(getMapping, "Method should be annotated with @GetMapping");
        assertTrue(getMapping.value().length > 0);
        assertEquals("/users/{id}", getMapping.value()[0], "Endpoint path should be /users/{id}");
    }

    @Test
    @DisplayName("Should have PathVariable annotation on validateUserExists parameter")
    void testValidateUserExistsParameter() throws NoSuchMethodException {
        Method method = UserClient.class.getMethod("validateUserExists", Long.class);
        
        Annotation[] annotations = method.getParameterAnnotations()[0];
        boolean hasPathVariable = false;
        
        for (Annotation annotation : annotations) {
            if (annotation instanceof PathVariable pathVariable) {
                hasPathVariable = true;
                assertEquals("id", pathVariable.value(), "PathVariable should map to 'id'");
            }
        }
        
        assertTrue(hasPathVariable, "Parameter should be annotated with @PathVariable");
    }
}
